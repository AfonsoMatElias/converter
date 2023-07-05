
<p align="center"><a href="#" target="_blank" rel="noopener noreferrer"><img height="120px" src="assets/images/Converter-272.png" /></a></p>

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

## Creating Mapping for each class type

We can also create mapping configuration for each class type, and add global options if needed.
The options can be added on **Mapping Configuration Creation** or after, it depends to you.

* Mapping Configuration Creation

```java
  // Converter Instance
  IConverter converter = new Converter();

  converter.createMap(Category.class, CategoryDto.class);

  converter.createMap(Product.class, ProductDto.class, (options) -> {
    
    options.beforeMap((src, dst) -> {
      // TODO: something nice 🤩 before the object is mapped
    });

    options.afterMap((src, dst) -> {
      // TODO: something nice 🤩 after the object is mapped
    });

  });
```

* After Mapping Configuration Creation, this way we can chain them

```java
  // Converter Instance
  IConverter converter = new Converter();

  converter.createMap(Category.class, CategoryDto.class);

  converter.createMap(Product.class, ProductDto.class)
    .beforeMap((src, dst) -> {
      // TODO: something nice 🤩 before the object is mapped
    })
    .afterMap((src, dst) -> {
      // TODO: something nice 🤩 after the object is mapped
    });
```

* If you also want to add reverse mapping for the entities, we use reverseMap to achieve that

```java
  // Converter Instance
  IConverter converter = new Converter();

  converter.createMap(Category.class, CategoryDto.class)
    .reverseMap();
```

* We can also mutate or transform a value types

```java
  // Converter Instance
  IConverter converter = new Converter();

  converter.addTransform(String.class, String[].class, (source) -> {

      String[] arrayOfStringValue = source.split(";");

      return arrayOfStringValue;
  });
```

* For self-reference objects we can limit how many objects we want to be returned using.
  By default is *3*.

```java

  // Converter Instance
  IConverter converter = new Converter();
  converter.setLimitCycleMapping(1);

```

* Using *forMember* method we can target a member and modify it value, and *skipMember* to avoid member mapping

```java
  // Converter Instance
  IConverter converter = new Converter();

  converter.createMap(User.class, UserDto.class)
    .forMember("name", (src) -> {
      return " Sr(a)." + src.getName();
    })
    .skipMember("password");
```

* Converting and modifying
```java
  // Converter Instance
  IConverter converter = new Converter();

  // Entities
  Product model = new Product();

  // Mapping
  ProductDto dto = converter.map(model).to(ProductDto.class, (options) -> {
     options.beforeMap((src, dst) -> {
      // TODO: something nice 🤩 before the object is mapped
    });

    options.afterMap((src, dst) -> {
      // TODO: something nice 🤩 after the object is mapped
    });
  });
```

### Using Spring Boot

If you use SpringBoot and want to use Dependency Injection, you can create a converter config file and assign it as *@Component*:

```java
  @Component
  public class ConverterConfig extends Converter {
    public ConverterConfig() {
      
      setLimitCycleMapping(1);
      createMap(Product.class, ProductDto.class);

    }
  }
```

* Injecting in the controller via DI

```java
  @Component
  public class ProductController {
    IConverter converter;
    
    @Autowired
    ProductService service;

    @Autowired
    public ProductController(Converter converter) {
      this.converter = converter;
    }

    @GetMapping(produces = "application/json", value = "/{id}")
    public ProductDto getOne(@PathVariable("id") Long id) {
      
      Product product = service.findById(id);

      ProductDto productDto = converter.map(product).to(ProductDto.class);

      return productDto;
    }

    @GetMapping(produces = "application/json")
    public List<ProductDto> getAll() {

      List<Product> products = service.findAll();
      
      List<ProductDto> productDtos = converter.map(products).to(ProductDto.class);

      return productDtos;
    }
  }
```