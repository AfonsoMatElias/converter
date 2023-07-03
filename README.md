# Converter v1.0


## Introduction

Converter is a Library used to convert/map an object to another, in a simple way without doing making to many steps to achieve the object conversion.

This Mapper Lib inspired in C# AutoMapper Library.

## Usage

```java

// Converter Instance
IConverter converter = new Converter();

// Entities
Product model = new Product();

// Mapping
ProductDto dto = converter.map(model).to(ProductDto.class);

```


### Using Spring Boot

If you use SpringBoot and want to use Dependency Injection, you can create a converter config file and assign it as component:

```java
@Component
public class ConverterConfig extends Converter {
  public ConverterConfig() {
  setLimitCycleMapping(1);

  createMappingConfig();
  }

  public void createMappingConfig() {
  createMap(Product.class, ProductDto.class);
  }
}

```

Injecting in the controller

```java

@Component
public class ProductController {
  IConverter converter;
  
  @Autowired
  ProductService service;

  @Autowired
  public TestController(Converter converter) {
    this.converter = converter;
  }

  public ProductDto getOne(Long id) {
    
    Product product = service.findById(id);

    ProductDto productDto = converter.map(product).to(ProductDto.class);

    return productDto;
  }

  public List<ProductDto> getAll() {

    List<Product> products = service.findAll();
    
    List<ProductDto> productDtos = converter.map(products).to(ProductDto.class);

    return productDtos;
  }
}

```

