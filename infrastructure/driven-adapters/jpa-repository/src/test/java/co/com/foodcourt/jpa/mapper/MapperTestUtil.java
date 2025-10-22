package co.com.foodcourt.jpa.mapper;


import org.mapstruct.factory.Mappers;
import java.util.Arrays;
public final class MapperTestUtil {

    private MapperTestUtil() {}

    @SuppressWarnings("unchecked")
    public static <T> T createMapperWithDependencies(Class<T> mapperClass) {
        T mapper = Mappers.getMapper(mapperClass);
        injectDependenciesRecursively(mapper);
        return mapper;
    }

    @SuppressWarnings("unchecked")
    private static void injectDependenciesRecursively(Object mapper) {
        Arrays.stream(mapper.getClass().getDeclaredFields()).forEach(field -> {
            try {
                Class<?> fieldType = field.getType();

                if (fieldType.getPackageName().contains("co.com.foodcourt.jpa.mapper")) {
                    Object dependency = Mappers.getMapper((Class<Object>) fieldType);
                    field.setAccessible(true);
                    field.set(mapper, dependency);

                    injectDependenciesRecursively(dependency);
                }

            } catch (Exception e) {
                throw new RuntimeException("Error al inyectar dependencia del mapper: " + field.getName(), e);
            }
        });
    }
}