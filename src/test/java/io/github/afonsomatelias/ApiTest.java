package io.github.afonsomatelias;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import io.github.afonsomatelias.Models.Product;
import io.github.afonsomatelias.Models.ProductDto;

/**
 * Unit test for simple Api.
 */
public class ApiTest {
    Map<String, Integer> funCallTracker = new HashMap<>();

    @Test
    public void shouldConvertFromModelToDto() {
        // Converter Instance
        IConverter converter = new Converter();

        // Entities
        Product model = new Product();
        model.setParent(model);

        // Mapping
        ProductDto dto = converter.map(model).to(ProductDto.class);

        assertTrue(dto != null);
        assertTrue(dto.getParent() != null);
        assertEquals(dto.getName(), model.getName());

        assertNotEquals(dto.getClass(), model.getClass());
    }

    @Test
    public void shouldConvertFromListModelToListDto() {
        // Converter Instance
        IConverter converter = new Converter();

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
    public void shouldCopyAndPasteModel() {
        // Converter Instance
        IConverter converter = new Converter();

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
        // Converter Instance
        IConverter converter = new Converter();

        // Transformacao de tipo no momento de converçao
        converter.addTransform(String.class, String[].class, (source) -> {

            String[] arrayOfStringValue = source.split(";");

            return arrayOfStringValue;
        });

        // Entities
        Product model = new Product();

        // Mapping
        ProductDto dto = converter.map(model).to(ProductDto.class);

        assertNotNull(dto.getCategories());
        assertEquals(2, dto.getCategories().length);
    }

    @Test
    public void shouldChangeMemberValueAccording() {
        // Converter Instance
        IConverter converter = new Converter();

        converter.createMap(Product.class, ProductDto.class)
            .forMember("name", (src) -> {
                return "Wine";
            });

        // Entities
        Product model = new Product();

        // Mapping
        ProductDto dto = converter.map(model).to(ProductDto.class);

        assertNotEquals(model.getName(), dto.getName());
        assertEquals(dto.getName(), "Wine");
    }

    @Test
    public void shouldChangeSetterMemberValueAccording() {
        // Converter Instance
        IConverter converter = new Converter();

        converter.createMap(Product.class, ProductDto.class)
            .forMember(ProductDto::setName, (src) -> {
                return "Wine";
            });

        // Entities
        Product model = new Product();

        // Mapping
        ProductDto dto = converter.map(model).to(ProductDto.class);

        assertNotEquals(model.getName(), dto.getName());
        assertEquals(dto.getName(), "Wine");
    }

    @Test
    public void shouldSkipMemberMapping() {
        // Converter Instance
        IConverter converter = new Converter();

        // Transformacao de tipo no momento de converçao
        converter.createMap(Product.class, ProductDto.class)
                .skipMember("name");

        // Entities
        Product model = new Product();

        // Mapping
        ProductDto dto = converter.map(model).to(ProductDto.class);

        assertNull(dto.getName());
    }

    @Test
    public void shouldSkipMemberMappingIfAllMembersAreNullReturnNull() {
        // Converter Instance
        IConverter converter = new Converter();

        // Transformacao de tipo no momento de converçao
        converter.createMap(Product.class, ProductDto.class)
                .skipMember("name")
                .skipMember("price");

        // Entities
        Product model = new Product();

        // Mapping
        ProductDto dto = converter.map(model).to(ProductDto.class);

        assertNull(dto);
    }

    @Test
    public void shouldCallBeforeMapActionWithSourceValueAndNullDestination() {
        // Converter Instance
        IConverter converter = new Converter();

        // Entities
        Product model = new Product();

        funCallTracker.put("function_calling_counter", 0);

        // Mapping
        ProductDto dto = converter.map(model).to(ProductDto.class, (options) -> {

            options.beforeMap((src, dst) -> {
                assertNotNull(src);
                assertNull(dst);
                funCallTracker.put("function_calling_counter", funCallTracker.get("function_calling_counter") + 1);
            });

        });

        assertNotNull(dto);
        assertTrue(funCallTracker.get("function_calling_counter") == 1);
    }

    @Test
    public void shouldCallAfterMapActionWithSourceValueAndDestination() {
        // Converter Instance
        IConverter converter = new Converter();

        // Entities
        Product model = new Product();

        funCallTracker.put("function_calling_counter", 0);

        // Mapping
        ProductDto dto = converter.map(model).to(ProductDto.class, (options) -> {

            options.afterMap((src, dst) -> {
                assertNotNull(src);
                assertNotNull(dst);
                funCallTracker.put("function_calling_counter", funCallTracker.get("function_calling_counter") + 1);
            });

        });

        assertNotNull(dto);
        assertTrue(funCallTracker.get("function_calling_counter") == 1);
    }

    @Test
    public void shouldCallGlobalBeforeMapAndAfterMapActions() {
        // Converter Instance
        IConverter converter = new Converter();
        funCallTracker.put("beforeMap_function_calling_counter", 0);
        funCallTracker.put("afterMap_function_calling_counter", 0);

        converter.createMap(Product.class, ProductDto.class, (options) -> {

            options.beforeMap((src, dst) -> {
                assertNotNull(src);
                assertNull(dst);
                funCallTracker.put("beforeMap_function_calling_counter",
                        funCallTracker.get("beforeMap_function_calling_counter") + 1);
            });

            options.afterMap((src, dst) -> {
                assertNotNull(src);
                assertNotNull(dst);
                funCallTracker.put("afterMap_function_calling_counter",
                        funCallTracker.get("afterMap_function_calling_counter") + 1);
            });

        });

        // Entities
        Product model = new Product();

        // Mapping
        ProductDto dto = converter.map(model).to(ProductDto.class);

        assertNotNull(dto);
        assertTrue(funCallTracker.get("beforeMap_function_calling_counter") == 1);
        assertTrue(funCallTracker.get("afterMap_function_calling_counter") == 1);
    }

    @Test
    public void shouldLimitSelfReferenceLayerToValueAssigned() {
        
        // Converter Instance
        IConverter converter = new Converter();
        converter.setLimitCycleMapping(1);

        // Entities
        Product model = new Product();
        model.setParent(model);

        // Mapping
        ProductDto dto = converter.map(model).to(ProductDto.class);

        assertTrue(dto.getParent() != null);
        assertTrue(dto.getParent().getParent() == null);
    }

    @Test
    public void shouldIgnoreAllSelfReferenceLayer() {
        
        // Converter Instance
        IConverter converter = new Converter();
        converter.setLimitCycleMapping(0);

        // Entities
        Product model = new Product();
        model.setParent(model);

        // Mapping
        ProductDto dto = converter.map(model).to(ProductDto.class);

        assertTrue(dto.getParent() == null);
    }

    @Test
    public void shouldNotMapIfSetToUseConfigAlways() {
        
        // Converter Instance
        IConverter converter = new Converter();
        converter.setUseMapConfiguration(true);

        // Entities
        Product model = new Product();
        model.setParent(model);

        // Mapping
        ProductDto dto = converter.map(model).to(ProductDto.class);

        assertTrue(dto == null);
    }

        @Test
    public void shouldCallBeforeEachMapActionWithSourceValueAndNullDestination() {
        // Converter Instance
        IConverter converter = new Converter();

        // Entities
        Product model = new Product();

        funCallTracker.put("function_calling_counter", 0);

        // Mapping
        List<ProductDto> dto = converter.map(Arrays.asList(model, model)).to(ProductDto.class, (options) -> {

            options.beforeEachMap((src, dst) -> {

                assertNotNull(src);
                assertNull(dst);
                funCallTracker.put("function_calling_counter", funCallTracker.get("function_calling_counter") + 1);
                
            });

        });

        assertNotNull(dto);
        assertTrue(funCallTracker.get("function_calling_counter") == 2);
    }

    @Test
    public void shouldCallAfterEachMapActionWithSourceValueAndDestination() {
        // Converter Instance
        IConverter converter = new Converter();

        // Entities
        Product model = new Product();

        funCallTracker.put("function_calling_counter", 0);

        // Mapping
        List<ProductDto> dto = converter.map(Arrays.asList(model, model)).to(ProductDto.class, (options) -> {

            options.afterEachMap((src, dst) -> {

                assertNotNull(src);
                assertNotNull(dst);
                funCallTracker.put("function_calling_counter", funCallTracker.get("function_calling_counter") + 1);

            });

        });

        assertNotNull(dto);
        assertTrue(funCallTracker.get("function_calling_counter") == 2);
    }

    @Test
    public void shouldExtractValueFromPropertiesOfAnotherObjectHavingTheSameNameAndMustBeHaveSameMemoryAddress() {
        // Converter Instance
        IConverter converter = new Converter();

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
    public void shouldSkipMemberOnExtractionUsingStringMember() {
        // Converter Instance
        IConverter converter = new Converter();

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
        // Converter Instance
        IConverter converter = new Converter();

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
}
