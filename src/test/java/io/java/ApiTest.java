package io.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import io.java.Models.Product;
import io.java.Models.ProductDto;


/**
 * Unit test for simple Api.
 */
public class ApiTest 
{

    @Test
    public void shouldConvertFromModelToDto()
    {
        // Converter Instance
        IConverter converter = new Converter();

        // Entities
        Product model = new Product();
        model.setParent(model);

        // Mapping
        ProductDto dto = converter.map(model).to(ProductDto.class);

        assertTrue( dto != null );
        assertTrue(dto.getParent() != null);
        assertEquals(dto.getName(), model.getName());
        
        assertNotEquals(dto.getClass(), model.getClass());
    }

    @Test
    public void shouldConvertFromListModelToListDto()
    {
        // Converter Instance
        IConverter converter = new Converter();

        // Entities
        Product model = new Product();
        model.setParent(model);
        List<Product> models = Arrays.asList(model);

        // Mapping
        List<ProductDto> dtos = converter.map(models).to(ProductDto.class);

        assertTrue( dtos != null );
        assertEquals(dtos.size(), models.size());
    }

    @Test
    public void shouldCopyAndPasteModel()
    {
        // Converter Instance
        IConverter converter = new Converter();

        // Entities
        Product model1 = new Product();
        model1.setParent(model1);

        // Mapping
        Product model2 = converter.map(model1).to();
        
        assertEquals(model2.getClass(), model1.getClass());
    }

    @Test
    public void shouldTransformeValue()
    {
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
    public void shouldSkiptMemberMapping()
    {
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

}
