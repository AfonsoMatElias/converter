package io.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import io.java.Models.Product;
import io.java.Models.ProductDto;


/**
 * Unit test for simple App.
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
        Product model = new Product();
        model.setParent(model);

        // Mapping
        Product model2 = converter.map(model).build();
        
        assertEquals(model2.getClass(), model.getClass());
    }

}
