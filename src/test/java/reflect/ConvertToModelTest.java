package reflect;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.ToString;
import org.junit.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
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
        System.out.printf("total time %o\n", System.currentTimeMillis() - startTime);


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
        System.out.printf("total time %o\n", System.currentTimeMillis() - startTime);


        //reflection only
        //total time 530
        CommonModel res3 = null;
        startTime = System.currentTimeMillis();
        for (int i = 0; i < loops; i++) {
            var res = adapter.convertToModelNoGsonReflectOnly(c);
            if (i == 0) {
                res3 = res;
            }
        }

        System.out.printf("total time %o\n", System.currentTimeMillis() - startTime);

        assertThat(res1)
                .isEqualTo(res2)
                .isEqualTo(res3);

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
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Foonatation {

    }

    public static class ModelAdapter<T extends Content> {

        private final Gson gson = new Gson();
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


        @SuppressWarnings({"java:S3011"})
        public T convertToModelNoGsonReflectOnly(Content content) {
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
                        field.set(resultModel, content.getData().get(field.getName()));
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
