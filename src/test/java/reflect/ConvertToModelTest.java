package reflect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import org.junit.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class ConvertToModelTest {

    @Test
    @SneakyThrows
    public void convertToModelTest() {

        var adapter = new ModelAdapter<>(CommonModel.class);
        var logMessage = "%-30s  stopwatch %oms %n";


        var c = getContent();
        long startTime;
        var loops = 1000000;

        //gson
        //total time 7257
        CommonModel res1 = null;
        startTime = System.currentTimeMillis();
        for (int i = 0; i < loops; i++) {
            var res = adapter.convertToModelWithGson(c);
            if (i == 0) {
                res1 = res;
            }
        }
        System.out.printf(logMessage, "gson", System.currentTimeMillis() - startTime);


        //gson, no annotation
        //total time 2444
        CommonModel res2 = null;
        startTime = System.currentTimeMillis();
        for (int i = 0; i < loops; i++) {
            var res = adapter.convertToModelNoAnnotations(c);
            if (i == 0) {
                res2 = res;
            }
        }
        System.out.printf(logMessage, "gson, no annotation", System.currentTimeMillis() - startTime);


        //reflection only
        //total time 530
        CommonModel res3 = null;
        startTime = System.currentTimeMillis();
        for (int i = 0; i < loops; i++) {
            var res = adapter.convertToModelManual(c);
            if (i == 0) {
                res3 = res;
            }
        }

        System.out.printf(logMessage, "manual mapping", System.currentTimeMillis() - startTime);

        //jackson
        CommonModel res4 = null;
        startTime = System.currentTimeMillis();
        for (int i = 0; i < loops; i++) {
            var res = adapter.convertToModelJackson(c);
            if (i == 0) {
                res4 = res;
            }
        }
        System.out.printf(logMessage, "jackson", System.currentTimeMillis() - startTime);


        System.out.println(res1);
        System.out.println(res2);
        System.out.println(res3);
        System.out.println(res4);

        assertThat(res1)
                .isEqualTo(res2)
                .isEqualTo(res3)
                .isEqualTo(res4);

    }

    private Content getContent() {
        var c = new Content();
        var data = new HashMap<String, Object>();
        c.setObjectId(UUID.randomUUID());
        c.setVersionId(UUID.randomUUID());

        c.setType("type");
        c.setShortTitle("title");
        c.setVersionNumber(3);
        c.setHasFiles(false);

        data.put("i1", 1);
        data.put("i2", 2);
        data.put("s1", "qewrt");
        data.put("s2", "frgthyjuki");
        data.put("s3", "sdghfjikgo");
        data.put("s4", "dyhujiol");
        data.put("s5", "wertyui");


        data.put("t1", new Timestamp((System.currentTimeMillis() / 1000) * 1000));

        var map11 = new LinkedHashMap<String, String>();
        map11.put("linkId", "52c8226d-5d88-4eb7-ba28-0e9804120911");
        map11.put("objectId", "7790b80f-93b3-4fc5-b149-bfef3278ae0e");

        var map12 = new LinkedHashMap<String, String>();
        map12.put("linkId", UUID.randomUUID().toString());
        map12.put("objectId", UUID.randomUUID().toString());

        var map2 = new LinkedHashMap<String, String>();
        map2.put("objectId", UUID.randomUUID().toString());

        data.put("l1", List.of(map11, map12));
        data.put("l2", List.of(map2));

        c.setData(data);

        c.setClassPath("1234.2345.456");
        c.setCreatedBy("admin");
        c.setDate(new Timestamp(System.currentTimeMillis()));
        c.setDeleted(false);
        c.setEntityCode("code");
        c.setModifiedBy("me");
        c.setModifyDate(new Timestamp(System.currentTimeMillis()));
        return c;
    }

    @Data
    public static class Content {
        @Foonatation
        private UUID objectId;

        @Foonatation
        private UUID versionId;

        @Foonatation
        private String type;

        @Foonatation
        private String classPath;

        @Foonatation
        private Boolean deleted;

        @Foonatation
        private Timestamp date;

        @Foonatation
        private String createdBy;

        @Foonatation
        private Map<String, Object> data = new HashMap<>();

        private String shortTitle;

        private String entityCode;

        @Foonatation
        private Integer versionNumber;

        @Foonatation
        private String modifiedBy;

        @Foonatation
        private Timestamp modifyDate;

        @Foonatation
        private boolean hasFiles;
    }

    @Data
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    public static class CommonModel extends Content {
        private Integer i1;
        private Integer i2;
        private String s1;
        private String s2;
        private String s3;
        private String s4;
        private String s5;
        private Timestamp t1;
        private ContentLinks l1;
        private ContentLinks l2;
    }

    public static class ContentLinks extends ArrayList<FieldRelation> {

        public List<UUID> getIds() {
            return this.stream().map(FieldRelation::getLinkId).collect(Collectors.toList());
        }

        public List<UUID> getLinkObjects() {
            return this.stream().map(FieldRelation::getObjectId).collect(Collectors.toList());
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldRelation {
        private UUID linkId;
        private UUID objectId;
        private UUID versionId;
    }


    @Retention(RetentionPolicy.RUNTIME)
    public @interface Foonatation {

    }

    public static class ModelAdapter<T extends Content> {

        private final Gson gson = new Gson();
        private final ObjectMapper mapper = new ObjectMapper();

        private final Class<T> modelClass;
        private final List<Field> modelFields;
        private final List<Field> contentFields;

        public ModelAdapter(Class<T> modelClass) {

            this.contentFields = Arrays.stream(Content.class.getDeclaredFields()).filter(field -> {
                Foonatation systemFieldSign = field.getAnnotation(Foonatation.class);
                return systemFieldSign != null;
            }).collect(Collectors.toList());
            this.modelClass = modelClass;
            modelFields = Arrays.stream(modelClass.getDeclaredFields())
                    .collect(Collectors.toList());
        }

        public CommonModel convertToModelWithGson(Content content) {
            CommonModel resultModel;
            try {
                JsonElement jsonData = gson.toJsonTree(content.getData());
                resultModel = gson.fromJson(jsonData, CommonModel.class);

                // Перенос системных полей объекта контента
                for (var field : Content.class.getDeclaredFields()) {
                    Foonatation systemFieldSign = field.getAnnotation(Foonatation.class);
                    if (systemFieldSign == null) continue;

                    field.setAccessible(true);
                    field.set(resultModel, field.get(content));
                }

                // Перенос общих полей
                for (var field : content.getClass().getFields()) {
                    field.setAccessible(true);
                    field.set(resultModel, field.get(content));
                }
            } catch (JsonSyntaxException | IllegalAccessException e) {
                throw new JsonSyntaxException(e.getLocalizedMessage(), e);
            }
            return resultModel;
        }

        public CommonModel convertToModelNoAnnotations(Content content) {
            CommonModel resultModel;
            try {
                JsonElement jsonData = gson.toJsonTree(content.getData());
                resultModel = gson.fromJson(jsonData, CommonModel.class);
                // Перенос системных полей объекта контента
                for (var field : contentFields) {
                    field.setAccessible(true);
                    field.set(resultModel, field.get(content));
                }
            } catch (JsonSyntaxException | IllegalAccessException e) {
                throw new JsonSyntaxException(e.getLocalizedMessage(), e);
            }
            return resultModel;
        }

        public CommonModel convertToModelJackson(Content content) {
            CommonModel resultModel;
            try {
                var tree = mapper.valueToTree(content.getData());
                resultModel = mapper.treeToValue(tree, CommonModel.class);
                // Перенос системных полей объекта контента
                for (var field : contentFields) {
                    field.setAccessible(true);
                    field.set(resultModel, field.get(content));
                }
            } catch (JsonSyntaxException | IllegalAccessException | JsonProcessingException e) {
                throw new JsonSyntaxException(e.getLocalizedMessage(), e);
            }
            return resultModel;
        }


        @SuppressWarnings({"java:S3011"})
        public T convertToModelManual(Content content) {
            final var errorMsg = "Error convert to model, type: %s, versionId: %s, error: %s";
            T resultModel = null;
            try {
                resultModel = modelClass.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }

            try {
                for (var field : modelFields) {
                    if (content.getData().containsKey(field.getName())) {
                        field.setAccessible(true);
                        if (field.getType().equals(ContentLinks.class)) {
                            List<HashMap<String, String>> links = (List<HashMap<String, String>>) content.getData().get(field.getName());
                            var ls = new ContentLinks();
                            for (var l : links) {
                                var fr = new FieldRelation();
                                if (l.containsKey("linkId"))
                                    fr.setLinkId(UUID.fromString(l.get("linkId")));
                                if (l.containsKey("objectId"))
                                    fr.setObjectId(UUID.fromString(l.get("objectId")));
                                if (l.containsKey("versionId"))
                                    fr.setVersionId(UUID.fromString(l.get("versionId")));
                                ls.add(fr);
                            }

                            field.set(resultModel, ls);
                        } else {
                            field.set(resultModel, content.getData().get(field.getName()));
                        }
                    }
                }
                // Перенос системных полей объекта контента
                for (var field : contentFields) {
                    field.setAccessible(true);
                    field.set(resultModel, field.get(content));
                }

            } catch (IllegalAccessException e) {
                System.out.printf(errorMsg, content.getType(), content.getVersionId(), e.getLocalizedMessage());
            }
            return resultModel;
        }
    }

}
