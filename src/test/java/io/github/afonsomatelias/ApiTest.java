package io.github.afonsomatelias;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import io.github.afonsomatelias.Configurations.ConverterConfiguration;
import io.github.afonsomatelias.Helpers.MethodCallCounter;
import io.github.afonsomatelias.Models.Product;
import io.github.afonsomatelias.Models.ProductDto;
import io.github.afonsomatelias.Models.ProductProjection;
import io.github.afonsomatelias.Models.User;
import io.github.afonsomatelias.Models.UserDto;
import io.github.afonsomatelias.Profiles.ProductProfile;
import io.github.afonsomatelias.Profiles.UserProfile;
import io.github.afonsomatelias.Resolvers.LocalDateTypeResolver;

/**
 * Unit test for simple Api.
 */
public class ApiTest {
    MethodCallCounter method;

    public ApiTest() {
        new ConverterConfiguration((config) -> {
            // config.setSilentLogs(true);
        });
    }

    @Before
    public void beforeTest() {
        method = MethodCallCounter.$new();
    }

    @Test
    public void shouldConvertFromModelToDto() {
        ConverterConfiguration config = new ConverterConfiguration();
        IConverter converter = config.createConverter();

        // Entities
        Product model = new Product();
        model.setParent(model);

        // Mapping
        ProductDto dto = converter.map(model).to(ProductDto.class);

        assertTrue(dto != null);
        assertTrue(dto.getParent() == dto);
        assertEquals(dto.getName(), model.getName());

        assertNotEquals(dto.getClass(), model.getClass());
    }

    @Test
    public void shouldConvertFromListModelToListDto() {
        ConverterConfiguration config = new ConverterConfiguration();
        IConverter converter = config.createConverter();

        // Entities
        Product model = new Product();
        model.setParent(model);
        List<Product> models = Arrays.asList(model);

        // Mapping
        List<ProductDto> dtos = converter.map(models).to(ProductDto.class);

        assertTrue(dtos != null);
        assertEquals(dtos.size(), models.size());
    }

    @Test
    @SuppressWarnings("unused")
    public void shouldConvertFromVirtualModelToDto() {
        ConverterConfiguration config = new ConverterConfiguration();
        IConverter converter = config.createConverter();

        class ProductInnerClass extends Product {

            public Float VAT;

            public ProductInnerClass() { super(); }

            public Float getVAT() {
                return VAT;
            }

            public void setVAT(Float VAT) {
                this.VAT = VAT;
            }
        }

        class ProductInnerClassDto extends ProductDto {

            public Float VAT;

            public ProductInnerClassDto() {
                super();
            }

            public Float getVAT() {
                return VAT;
            }

            public void setVAT(Float VAT) {
                this.VAT = VAT;
            }
        }

        // Entities
        ProductInnerClass model = new ProductInnerClass();
        model.setParent(model);

        // Mapping
        ProductInnerClassDto dto = converter.map(model).to(ProductInnerClassDto.class);

        assertTrue(dto != null);
        assertTrue(dto.getParent() == dto);
        assertEquals(dto.getName(), model.getName());

        assertNotEquals(dto.getClass(), model.getClass());
    }

    @Test
    public void shouldCopyAndPasteModel() {
        ConverterConfiguration config = new ConverterConfiguration();
        IConverter converter = config.createConverter();

        // Entities
        Product model1 = new Product();
        model1.setParent(model1);

        // Mapping
        Product model2 = converter.map(model1).to();

        assertEquals(model2.getClass(), model1.getClass());
        assertNotEquals(model2.toString(), model1.toString());
    }

    @Test
    public void shouldTransformeValue() {
        ConverterConfiguration config = new ConverterConfiguration();

        // Addin a type stransformation
        config.addTransform(String.class, String[].class, (source) -> {

            String[] arrayOfStringValue = source.split(";");

            return arrayOfStringValue;
        });

        IConverter converter = config.createConverter();

        // Entities
        Product model = new Product();

        // Mapping
        ProductDto dto = converter.map(model).to(ProductDto.class);

        assertNotNull(dto.getCategories());
        assertEquals(2, dto.getCategories().length);
    }

    @Test
    public void shouldChangeMemberValueAccordingForMemberValue() {
        ConverterConfiguration config = new ConverterConfiguration();

        config.createMap(Product.class, ProductDto.class)
                .forMember("name", (src) -> {
                    return "Wine";
                });

        IConverter converter = config.createConverter();

        // Entities
        Product model = new Product();

        // Mapping
        ProductDto dto = converter.map(model).to(ProductDto.class);

        assertNotEquals(model.getName(), dto.getName());
        assertEquals(dto.getName(), "Wine");
    }

    @Test
    public void shouldChangeSetterMemberValueAccording() {
        ConverterConfiguration config = new ConverterConfiguration();

        config.createMap(Product.class, ProductDto.class)
                .forMember(ProductDto::setName, (src) -> {
                    return "Wine";
                });

        IConverter converter = config.createConverter();

        // Entities
        Product model = new Product();

        // Mapping
        ProductDto dto = converter.map(model).to(ProductDto.class);

        assertNotEquals(model.getName(), dto.getName());
        assertEquals(dto.getName(), "Wine");
    }

    @Test
    public void shouldMapFieldUsingForMemberMapInstance() {
        ConverterConfiguration config = new ConverterConfiguration();

        config.createMap(Product.class, ProductDto.class)
                .forMember(ProductDto::setForMemberMapTestChild, (src, cvtr) -> {
                    return cvtr.map(src).to(ProductDto.class);
                });

        IConverter converter = config.createConverter();

        // Entities
        Product model = new Product();

        // Mapping
        ProductDto dto = converter.map(model).to(ProductDto.class);

        assertNotNull(dto);
        assertEquals(model.getName(), dto.getName());
        assertNotNull(dto.getForMemberMapTestChild());
        assertEquals(model.getName(), dto.getForMemberMapTestChild().getName());
    }

    @Test
    public void shouldSkipMemberMapping() {
        ConverterConfiguration config = new ConverterConfiguration();

        config.createMap(Product.class, ProductDto.class)
                .skipMember("name");

        IConverter converter = config.createConverter();

        // Entities
        Product model = new Product();

        // Mapping
        ProductDto dto = converter.map(model).to(ProductDto.class);

        assertNull(dto.getName());
    }

    @Test
    public void shouldSkipMemberMappingIfAllMembersAreNullReturnNull() {
        ConverterConfiguration config = new ConverterConfiguration();

        config.createMap(Product.class, ProductDto.class)
                .skipMember("name")
                .skipMember("price");

        IConverter converter = config.createConverter();

        // Entities
        Product model = new Product();

        // Mapping
        ProductDto dto = converter.map(model).to(ProductDto.class);

        assertNull(dto);
    }

    @Test
    public void shouldCallBeforeMapActionWithSourceValueAndDestination() {
        ConverterConfiguration config = new ConverterConfiguration();
        IConverter converter = config.createConverter();

        // Entities
        Product model = new Product();

        // Mapping
        ProductDto dto = converter.map(model).to(ProductDto.class, (options) -> {
            options.beforeMap((src, dst) -> {
                assertNotNull(src);
                assertNull(dst);

                method.call();
            });
        });
        
        assertNotNull(dto);
        method.assertMethodCalled(1);
    }

    @Test
    public void shouldCallAfterMapActionWithSourceValueAndDestination() {
        ConverterConfiguration config = new ConverterConfiguration();
        IConverter converter = config.createConverter();

        // Entities
        Product model = new Product();

        // Mapping
        ProductDto dto = converter.map(model).to(ProductDto.class, (options) -> {

            options.afterMap((src, dst) -> {
                assertNotNull(src);
                assertNotNull(dst);

                method.call();
            });

        });

        assertNotNull(dto);
        method.assertMethodCalled(1);
    }

    @Test
    public void shouldCallGlobalBeforeMapAndAfterMapActions() {
        ConverterConfiguration config = new ConverterConfiguration();
        
        config.createMap(Product.class, ProductDto.class, (options) -> {

            options.beforeMap((src, dst) -> {
                assertNotNull(src);
                assertNull(dst);

                method.call();
            });

            options.afterMap((src, dst) -> {
                assertNotNull(src);
                assertNotNull(dst);

                method.call();
            });

        });

        IConverter converter = config.createConverter();

        // Entities
        Product model = new Product();

        // Mapping
        ProductDto dto = converter.map(model).to(ProductDto.class);

        assertNotNull(dto);
        method.assertMethodCalled(2);
    }

    @Test
    public void shouldHaveTheSameReferencesOnMappingTheSameObject() {
        ConverterConfiguration config = new ConverterConfiguration();
        IConverter converter = config.createConverter();

        // Entities
        Product model = new Product();
        model.setName("ABC");
        model.setParent(model);
        model.setProducts(Arrays.asList(model));

        // Mapping
        ProductDto dto = converter.map(model).to(ProductDto.class);

        assertTrue(dto.getParent() == dto);
    }

    @Test
    public void shouldNotMapIfSetToUseConfigAlways() {
        ConverterConfiguration config = new ConverterConfiguration();

        config.setUseMapConfiguration(true);

        // Converter Instance
        IConverter converter = config.createConverter();

        // Entities
        Product model = new Product();
        model.setParent(model);

        // Mapping
        ProductDto dto = converter.map(model).to(ProductDto.class);

        assertNull(dto);
    }

    @Test
    public void shouldCallBeforeEachMapActionWithSourceValueAndDestination() {
        ConverterConfiguration config = new ConverterConfiguration();
        IConverter converter = config.createConverter();

        // Entities
        Product model = new Product();

        // Mapping
        List<ProductDto> dto = converter.map(Arrays.asList(model, model)).to(ProductDto.class, (options) -> {

            options.beforeEachMap((src, dst) -> {

                assertNotNull(src);
                assertNull(dst);

                method.call();

            });

            options.afterEachMap((src, dst) -> {

                assertNotNull(src);
                assertNotNull(dst);

                method.call();
            });

        });

        assertNotNull(dto);

        method.assertMethodCalled(4);
    }
    
    @Test
    public void shouldCallAllTheMappingActionsAccordingToTheActionAndNumberItems() {
        ConverterConfiguration config = new ConverterConfiguration();
        IConverter converter = config.createConverter();

        // Entities
        Product model = new Product();

        // Mapping
        List<ProductDto> dto = converter.map(Arrays.asList(model, model)).to(ProductDto.class, (options) -> {

            options.beforeMap((src, dst) -> {

                assertNotNull(src);
                assertNull(dst);
                method.call();

            });

            options.afterMap((src, dst) -> {

                assertNotNull(src);
                assertNotNull(dst);
                method.call();

            });
            
            options.beforeEachMap((src, dst) -> {

                assertNotNull(src);
                assertNull(dst);
                method.call();

            });

            options.afterEachMap((src, dst) -> {

                assertNotNull(src);
                assertNotNull(dst);
                method.call();

            });

        });

        assertNotNull(dto);
        method.assertMethodCalled(6);
    }

    @Test
    public void shouldCallAfterEachMapActionWithSourceValueAndDestination() {
        ConverterConfiguration config = new ConverterConfiguration();
        IConverter converter = config.createConverter();

        // Mapping
        List<ProductDto> dto = converter.map(Arrays.asList(new Product(), new Product())).to(ProductDto.class, (options) -> {

            options.afterEachMap((src, dst) -> {

                assertNotNull(src);
                assertNotNull(dst);

                method.call();

            });

        });

        assertNotNull(dto);
        method.assertMethodCalled(2);
    }

    @Test
    public void shouldExtractValueFromPropertiesOfAnotherObjectHavingTheSameNameAndMustBeHaveSameMemoryAddress() {
        ConverterConfiguration config = new ConverterConfiguration();
        IConverter converter = config.createConverter();

        // Entities
        Product model = new Product();
        model.setParent(model);

        ProductDto dto = new ProductDto();
        dto.setName("Sprite");
        dto.setPrice(1f);

        Product modelMapped = converter.map(model).from(dto);

        assertTrue(modelMapped != null);
        assertTrue(modelMapped == model);
        assertSame(modelMapped, model);
        assertEquals(modelMapped.getName(), dto.getName());
        assertEquals(modelMapped.getPrice(), dto.getPrice());
        assertEquals(modelMapped, model);
    }

    @Test
    public void shouldExtractValueFromPropertiesOfAnotherObjectHavingTheSameNameAndMustBeHaveSameMemoryAddressApplyingOptions() {
        ConverterConfiguration config = new ConverterConfiguration();
        IConverter converter = config.createConverter();

        // Entities
        Product model = new Product();
        model.setParent(model);

        ProductDto dto = new ProductDto();
        dto.setName("Coca-Cola");
        dto.setPrice(2f);

        Product modelMapped = converter.map(model).from(dto, (options) -> {
            options.afterMap((srcDto, dstDb) -> {
                method.call();
            });
        });

        assertTrue(modelMapped != null);
        method.assertMethodCalled(1);
    }

    @Test
    public void shouldSkipMemberOnExtractionUsingStringMember() {
        ConverterConfiguration config = new ConverterConfiguration();
        IConverter converter = config.createConverter();

        // Entities
        Product model = new Product();
        model.setParent(model);

        ProductDto dto = new ProductDto();
        dto.setName("Sprite");
        dto.setPrice(1f);

        Product modelMapped = converter.map(model).from(dto, (options) -> {
            options.skipMembers("price");
        });

        assertTrue(modelMapped != null);
        assertTrue(modelMapped == model);
        assertSame(modelMapped, model);
        assertEquals(modelMapped.getName(), dto.getName());
        assertNotEquals(modelMapped.getPrice(), dto.getPrice());
        assertEquals(modelMapped, model);
    }

    @Test
    public void shouldSkipMemberOnExtractionUsingFieldMember() {
        ConverterConfiguration config = new ConverterConfiguration();
        IConverter converter = config.createConverter();

        // Entities
        Product model = new Product();
        model.setParent(model);

        ProductDto dto = new ProductDto();
        dto.setName("Sprite");
        dto.setPrice(1f);

        Product modelMapped = converter.map(model).from(dto, (options) -> {
            try {
                options.skipMembers(Product.class.getDeclaredField("price"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        assertTrue(modelMapped != null);
        assertTrue(modelMapped == model);
        assertSame(modelMapped, model);
        assertEquals(modelMapped.getName(), dto.getName());
        assertNotEquals(modelMapped.getPrice(), dto.getPrice());
        assertEquals(modelMapped, model);
    }

    @Test
    public void shouldSkipTypeMappingAccordingGlobalConfigAsClassType() {
        ConverterConfiguration config = new ConverterConfiguration();
        
        config.skipTypes(Float.class);

        IConverter converter = config.createConverter();

        // Entities
        Product model = new Product();
        model.setName("Sprite");
        model.setPrice(1f);

        ProductDto dto = converter.map(model).to(ProductDto.class);

        assertTrue(dto != null);
        assertTrue(dto.getPrice() == null);
    }

    @Test
    public void shouldSkipTypeMappingAccordingGlobalConfigAsStringName() {
        ConverterConfiguration config = new ConverterConfiguration();
        config.skipTypes("Float");

        IConverter converter = config.createConverter();

        // Entities
        Product model = new Product();
        model.setName("Sprite");
        model.setPrice(1f);

        ProductDto dto = converter.map(model).to(ProductDto.class);

        assertNotNull(dto);
        assertNull(dto.getPrice());
    }

    @Test
    public void shouldSkipTypeMappingAccordingToMappingOptions() {
        ConverterConfiguration config = new ConverterConfiguration();
        IConverter converter = config.createConverter();

        // Entities
        Product model = new Product();
        model.setName("Sprite");
        model.setPrice(1f);
        model.setQuantity(15);

        ProductDto dto = converter.map(model).to(ProductDto.class, (options) -> {
            options.skipTypes(String.class);
            options.skipTypes("Float");
        });

        assertNotNull(dto);
        assertNull(dto.getName());
        assertNull(dto.getPrice());
        assertNotNull(dto.getQuantity() != null);
    }

    @Test
    public void shouldAddTheProfilesAndUseTheConfiguration() {
        ConverterConfiguration config = new ConverterConfiguration((options) -> {
            
            options.setUseMapConfiguration(true);
            options.addProfile(
                ProductProfile.class,
                UserProfile.class
            );

        });

        IConverter converter = config.createConverter();

        // Entities
        Product product = new Product();
        product.setName("Sprite");
        product.setPrice(3f);

        User user = new User();

        ProductDto productDto = converter.map(product).to(ProductDto.class);
        UserDto userDto = converter.map(user).to(UserDto.class);

        assertNotNull(productDto);
        assertNotNull(userDto);
    }
    
    @Test
    public void shouldNotMapAnyTypeWithNoConfigAndUseMapConfigAsTrue() {
        ConverterConfiguration config = new ConverterConfiguration((options) -> {
            options.setUseMapConfiguration(true);
        });

        IConverter converter = config.createConverter();

        // Entities
        Product product = new Product();
        product.setName("Sprite");
        product.setPrice(3f);

        User user = new User();

        ProductDto productDto = converter.map(product).to(ProductDto.class);
        UserDto userDto = converter.map(user).to(UserDto.class);

        assertNull(productDto);
        assertNull(userDto);
    }

    @Test
    public void shouldProjectLinkedHashMapToTheProvidedClass() {

        ConverterConfiguration config = new ConverterConfiguration();
        IConverter converter = config.createConverter();

        LinkedHashMap<String, Object> product = new LinkedHashMap<String, Object>() {{
            put("name", "Sprite");
            put("price", 1f);
            put("quantity", 15);
        }};

        ProductDto dto = converter.project(product).to(ProductDto.class);

        assertNotNull(dto);
    }
    
    @Test
    public void shouldProjectAndResolveDefaultTypesIfSourceFieldIsString() {

        ConverterConfiguration config = new ConverterConfiguration();
        IConverter converter = config.createConverter();

        LinkedHashMap<String, Object> product = new LinkedHashMap<String, Object>() {{
            put("name", "Sprite");
            put("price", "1");
            put("quantity", "15");
        }};

        ProductDto dto = converter.project(product).to(ProductDto.class);

        assertNotNull(dto);
    }

    @Test
    public void shouldProjectLinkedHashMapWithNestedLinkedHashMap() {

        ConverterConfiguration config = new ConverterConfiguration();
        IConverter converter = config.createConverter();

        LinkedHashMap<String, Object> product = new LinkedHashMap<String, Object>() {{
            put("name", "Sprite");
            put("price", 1f);
            put("quantity", 15);

            put("parent", new LinkedHashMap<String, Object>() {{
                put("name", "Water");
                put("price", 0.5f);
                put("quantity", 35);
            }});

            put("products", Arrays.asList(
                new LinkedHashMap<String, Object>() {{
                    put("name", "Cola-Cola");
                    put("price", 1f);
                    put("quantity", 40);
                }},
                new LinkedHashMap<String, Object>() {{
                    put("name", "Beer");
                    put("price", 2f);
                    put("quantity", 12);
                }}
            ));
        }};

        ProductDto dto = converter.project(product).to(ProductDto.class);
        
        assertNotNull(dto);
        assertNotNull(dto.getParent());
    }
    
    @Test
    public void shouldProjectLinkedHashMapFromStringToLocalDate() {

        ConverterConfiguration config = new ConverterConfiguration();
        IConverter converter = config.createConverter();

        LinkedHashMap<String, Object> user = new LinkedHashMap<String, Object>() {{
            put("name", "John Doe");
            put("username", "johndoe");
            put("password", "123.AbC");
            put("bithdate", "1989-10-15");
            put("roles", new String[]{ "ADMIN" });
        }};

        UserDto dto = converter.project(user).to(UserDto.class);
        
        assertNotNull(dto);
    }

    @Test
    public void shouldNotProjectLinkedHashMapItemIfSourceValueCanNotBeResolved() {

        ConverterConfiguration config = new ConverterConfiguration();
        IConverter converter = config.createConverter();

        LinkedHashMap<String, Object> user = new LinkedHashMap<String, Object>() {{
            put("name", "John Doe");
            put("username", "johndoe");
            put("password", "123.AbC");
            put("bithdate", "2025-03-25T22:44:17.605Z");
            put("roles", new String[]{ "ADMIN" });
        }};

        UserDto dto = converter.project(user).to(UserDto.class);
        
        assertNotNull(dto);
        assertNull(dto.getBithdate());
    }

    @Test
    public void shouldProjectLinkedHashMapAndUseFunctionResolver() {

        ConverterConfiguration config = new ConverterConfiguration(options -> {

            // Resolve the LocalDate type using function
            options.use(LocalDate.class, (value) -> {
                if (!(value instanceof String)) return null;
                
                String str = value.toString();
                return LocalDate.parse(str.split("T")[0]);
            });

        });
        IConverter converter = config.createConverter();

        LinkedHashMap<String, Object> user = new LinkedHashMap<String, Object>() {{
            put("name", "John Doe");
            put("username", "johndoe");
            put("password", "123.AbC");
            put("bithdate", "2025-03-25T22:44:17.605Z");
            put("roles", new String[]{ "ADMIN" });
        }};

        UserDto dto = converter.project(user).to(UserDto.class);
        
        assertNotNull(dto);
        assertNotNull(dto.getBithdate());
    }

    @Test
    public void shouldProjectLinkedHashMapAndUseClassTypeResolver() {

        ConverterConfiguration config = new ConverterConfiguration(options -> {
            // Resolve the LocalDate type using ClassType
            options.use(LocalDateTypeResolver.class);
        });
        IConverter converter = config.createConverter();

        LinkedHashMap<String, Object> user = new LinkedHashMap<String, Object>() {{
            put("name", "John Doe");
            put("username", "johndoe");
            put("password", "123.AbC");
            put("bithdate", "2025-03-25T22:44:17.605Z");
            put("roles", new String[]{ "ADMIN" });
        }};

        UserDto dto = converter.project(user).to(UserDto.class);
        
        assertNotNull(dto);
        assertNotNull(dto.getBithdate());
    }

    @Test
    public void shouldProjectLinkedHashMapAndCallTheModifiers() {

        ConverterConfiguration config = new ConverterConfiguration();
        IConverter converter = config.createConverter();

        LinkedHashMap<String, Object> user = new LinkedHashMap<String, Object>() {{
            put("name", "John Doe");
            put("username", "johndoe");
            put("password", "123.AbC");
            put("bithdate", "1989-10-15");
            put("roles", new String[]{ "ADMIN" });
        }};

        UserDto dto = converter.project(user).to(UserDto.class, (options) -> {
            options.beforeMap((src, dst) -> {
                method.call();
            });

            options.afterMap((src, dst) -> {
                method.call();
            });
        });
        
        assertNotNull(dto);
        method.assertMethodCalled(2);
    }

    @Test
    public void shouldProjectLinkedHashMapAndSkipMembersAndTypes() {
        ConverterConfiguration config = new ConverterConfiguration();
        IConverter converter = config.createConverter();

        LinkedHashMap<String, Object> user = new LinkedHashMap<String, Object>() {{
            put("name", "John Doe");
            put("username", "johndoe");
            put("password", "123.AbC");
            put("bithdate", "2025-03-25T22:44:17.605Z");
            put("roles", new String[]{ "ADMIN" });
        }};

        UserDto dto = converter.project(user).to(UserDto.class, (options) -> {
            options.skipTypes(LocalDate.class);
            options.skipMembers("password");
        });
        
        assertNull(dto.getPassword());
        assertNull(dto.getBithdate());
    }

    @Test
    public void shouldProjectAnInterfaceToDto() {
        ConverterConfiguration config = new ConverterConfiguration();
        IConverter converter = config.createConverter();

        ProductProjection productProjection = new ProductProjection() {
            @Override
            public String getName() { return "Coca Cola"; }
            @Override
            public Float getPrice() { return 0.5f; }

            @Override
            public String[] getCategories() { return new String[]{ "Liquid", "Refrigerator" }; }
            @Override
            public Integer getQuantity() { return 15; }
        };

        ProductDto dto = converter.project(productProjection).to(ProductDto.class);

        assertNotNull(dto);
        assertNotNull(dto.getName());
    }
    
    @Test
    public void shouldProjectListInterfaceToDto() {
        ConverterConfiguration config = new ConverterConfiguration();
        IConverter converter = config.createConverter();

        ProductProjection productProjection1 = new ProductProjection() {
            @Override
            public String getName() { return "Coca Cola"; }
            @Override
            public Float getPrice() { return 0.5f; }

            @Override
            public String[] getCategories() { return new String[]{ "Liquid", "Refrigerator" }; }
            @Override
            public Integer getQuantity() { return 15; }
        };

        ProductProjection productProjection2 = new ProductProjection() {
            @Override
            public String getName() { return "Beer"; }
            @Override
            public Float getPrice() { return 0.6f; }

            @Override
            public String[] getCategories() { return new String[]{ "Liquid", "Alcoholic" }; }
            @Override
            public Integer getQuantity() { return 25; }
        };

        List<ProductDto> dtos = converter.project(Arrays.asList(productProjection1, productProjection2)).to(ProductDto.class);

        assertNotNull(dtos);

        assertSame(dtos.get(0).getName(), productProjection1.getName());
        assertSame(dtos.get(1).getName(), productProjection2.getName());
    }

    @Test
    public void shouldProjectAnInterfaceToDtoWithModifiers() {
        ConverterConfiguration config = new ConverterConfiguration();
        IConverter converter = config.createConverter();

        ProductProjection productProjection = new ProductProjection() {
            @Override
            public String getName() { return "Coca Cola"; }
            @Override
            public Float getPrice() { return 0.5f; }

            @Override
            public String[] getCategories() { return new String[]{ "Liquid", "Refrigerator" }; }
            @Override
            public Integer getQuantity() { return 15; }
        };

        ProductDto dto = converter.project(productProjection).to(ProductDto.class, (options) -> {
            options.skipMembers("setName");
            options.skipTypes(Integer.class);

            options.beforeMap((src, dst) -> {
                method.call();
            });

            options.afterMap((src, dst) -> {
                method.call();
            });
        });

        assertNotNull(dto);
        assertNull(dto.getName());
        assertNull(dto.getQuantity());
        method.assertMethodCalled(2);
    }
}
