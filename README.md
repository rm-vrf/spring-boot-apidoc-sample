# spring-boot-apidoc-sample

## 设计原则

以下设计原则摘自：https://docs.microsoft.com/zh-cn/azure/architecture/best-practices/api-design

Leonard Richardson 提议对 Web API 使用以下成熟度模型：

- 级别 0：定义一个 URI，所有操作是对此 URI 发出的请求；
- 级别 1：为各个资源单独创建 URI；
- 级别 2：使用 HTTP 方法来定义对资源执行的操作；
- 级别 3：使用超媒体（HATEOAS）。

详细描述请看：https://martinfowler.com/articles/richardsonMaturityModel.html

发布的 Web API 要达到级别 2. 具体要求如下：

### 围绕资源组织 API

侧重于 Web API 公开的业务实体，资源 URI 应基于名词（资源）而不是动词（对资源执行的操作）。比如下面这个 URI 是合理的：

```
https://adventure-works.com/orders
```

而这个是不合理的：

```
https://adventure-works.com/create-order
```

避免创建反映数据库内部结构的 API。REST 旨在为实体建模，以及为应用程序可对这些实体执行的操作建模。不应将内部实现公开给客户端。

在 URI 中采用一致的命名约定。一般而言，有效的做法是对引用集合的 URI 使用复数名词。最好是将集合和项的 URI 组织成层次结构。例如，`/customers` 是客户集合的路径，`/customers/5` 是 ID 为 `5` 的客户的路径。这种方法有助于使 Web API 保持直观。

还需要考虑不同类型的资源之间的关系，以及如何公开这些关联。例如，`/customers/5/orders` 可以表示客户 `5` 的所有订单。但是要避免关联关系过于复杂（例如 `/customers/1/orders/99/products`），应用程序获取对某个资源的引用后，应该可以使用此引用去查找与该资源相关的项目。可将前面的查询替换为 `/customers/1/orders` 以查找客户 `1` 的所有订单，然后替换为 `/orders/99/products` 以查找此订单中的产品。

尽量避免使用公开大量小型资源的“琐碎” Web API。此类 API 可能需要客户端应用程序发送多个请求才能找到它需要的所有数据。建议将数据非规范化，并将相关信息合并成可通过单个请求检索的较大资源。

可能无法将 Web API 实现的每个操作都映射到特定资源。可以通过 HTTP 请求处理此类非资源场景，这些请求调用某个函数并将结果作为 HTTP 响应消息返回。例如，实现简单计算器操作（例如，加法和减法）的 Web API 可以提供公开这些操作作为伪资源的 URI，并使用查询字符串来指定所需的参数。例如 `/add?operand1=99&operand2=1`.

### 根据 HTTP 方法定义操作

HTTP 协议定义了大量为请求赋于语义的方法。大多数 RESTful Web API 使用的常见 HTTP 方法是：

- GET 检索位于指定 URI 处的资源的表示形式。响应消息的正文包含所请求资源的详细信息。
- POST 在指定的 URI 处创建新资源。请求消息的正文将提供新资源的详细信息。请注意，POST 还用于触发不实际创建资源的操作。
- PUT 在指定的 URI 处创建或替换资源。请求消息的正文指定要创建或更新的资源。
- PATCH 对资源执行部分更新。请求正文包含要应用到资源的一组更改。
- DELETE 删除位于指定 URI 处的资源。

### 符合 HTTP 语义

#### 媒体类型

媒体类型是客户端和服务器交换资源的表示形式。格式是使用媒体类型（也称为 MIME 类型）指定的。对于非二进制数据，Web API 支持 JSON（媒体类型 = `application/json`），可能还支持 XML（媒体类型 = `application/xml`）。

请求或响应中的 `Content-Type` 标头指定表示形式的格式。下面是包含 JSON 数据的 POST 请求示例：

```
POST /orders HTTP/1.1
Content-Type: application/json; charset=utf-8
Content-Length: 57
```

如果服务器不支持媒体类型，则应返回 HTTP 状态代码 `415`（不支持的媒体类型）。

客户端请求可以包含一个 `Accept` 标头，该标头包含客户端可以接受的、来自服务器的响应消息中的媒体类型列表。例如：

```
GET /orders/2 HTTP/1.1
Accept: application/json
```

如果服务器无法匹配所列的任何媒体类型，应返回 HTTP 状态代码 `406`（不可接受）。

#### GET 方法

成功的 GET 方法通常返回 HTTP 状态代码 `200`（正常）。如果找不到资源，该方法应返回 `404`（未找到）。

#### POST 方法

如果 POST 方法创建了新资源，则会返回 HTTP 状态代码 `200`（正常）。新资源的 URI 包含在响应的 `Location` 标头中。响应正文包含资源的表示形式。
如果该方法执行了一些处理但未创建新资源，则可以返回 HTTP 状态代码 `201`（已创建），并在响应正文中包含操作结果。或者，如果没有可返回的结果，该方法可以返回 HTTP 状态代码 `204`（无内容）但不返回任何响应正文。
如果客户端将无效数据放入请求，服务器应返回 HTTP 状态代码 `400`（错误的请求）。响应正文可以包含有关错误的其他信息，或包含可提供更多详细信息的 URI 链接。

#### PUT 方法

如果 PUT 方法创建了新资源，则会返回 HTTP 状态代码 `201`（已创建）。如果该方法更新了现有资源，则会返回 `200`（正常）或 `204`（无内容）。在某些情况下，可能无法更新现有资源。在这种情况下，可考虑返回 HTTP 状态代码 `409`（冲突）。

考虑实现可批量更新集合中的多个资源的批量 HTTP PUT 操作。

#### PATCH 方法

客户端可以使用 PATCH 请求向现有资源发送一组修补文档形式的更新。服务器将处理该修补文档以执行更新。修补文档不会描述整个资源，而只描述一组要应用的更改。

#### DELETE 方法

如果删除操作成功，Web 服务器应以 HTTP 状态代码 `204` 做出响应，指示已成功处理该过程，但响应正文不包含其他信息。如果资源不存在，Web 服务器可以返回 HTTP `404`（未找到）。

#### 异步操作

有时，操作可能需要经过一段时间的处理才能完成。如果需要等待该操作完成后才能向客户端发送响应，可能会造成不可接受的延迟。在这种情况下，将该操作设置为异步操作。返回 HTTP 状态代码 `202`（已接受），指示该请求已接受进行处理，但尚未完成。

公开一个可返回异步请求状态的终结点，使客户端能够通过轮询状态终结点来监视状态。在 `202` 响应的 `Location` 标头中包含状态终结点的 URI。例如：

```
HTTP/1.1 202 Accepted
Location: /api/status/12345
```

客户端向此终结点发送 GET 请求，响应中应包含该请求的当前状态。（可选）响应中还可以包含预计完成时间，或者用于取消操作的链接。

```
HTTP/1.1 200 OK
Content-Type: application/json

{
    "status":"In progress",
    "link": { "rel":"cancel", "method":"delete", "href":"/api/status/12345" }
}
```

### 数据筛选和分页

对集合资源执行的 GET 请求可能返回大量的项。要限制任何单个请求返回的数据量，支持最大项数和起始偏移量。例如：

```
/orders?size=25&from=50
```

通过提供一个将字段名称用作值的 `sort` 参数（例如 `/orders?sort=ProductID`），对提取的数据排序。

如果每个项包含大量数据，可以限制每个项返回的字段。例如，可以使用逗号分隔的字段列表，例如 `/orders?fields=ProductID,Quantity`.

为筛选和分页参数设置合理的默认值。

### 支持大型资源的部分响应

资源可能包含大型二进制字段，例如文件或图像，要分块检索此类资源。对于针对大型资源发出的 GET 请求，应支持 `Accept-Ranges` 标头。

`Content-Length` 标头指定资源的总大小，`Accept-Ranges` 标头指示相应的 GET 操作支持部分结果。客户端应用程序可以使用此信息以较小的区块检索图像。例如，通过使用 `Range` 标头提取前 2500 个字节：

```
GET /products/10?fields=productImage HTTP/1.1
Range: bytes=0-2499
```

响应消息通过返回 HTTP 状态代码 `206` 指示这是部分响应。`Content-Length` 标头指定消息正文中返回的实际字节数（不是资源的大小），`Content-Range` 标头指示这是该资源的哪一部分（第 0 到 2499 字节，总共 4580 个字节）:

```
HTTP/1.1 206 Partial Content

Accept-Ranges: bytes
Content-Type: image/jpeg
Content-Length: 2500
Content-Range: bytes 0-2499/4580
```

对这些资源实现 HTTP HEAD 请求。HEAD 请求与 GET 请求类似，不过，前者只返回描述资源的 HTTP 标头和空消息正文。

### 进行版本控制

随着业务需求的变化，接口可能更改。必须考虑此类更改对客户端应用造成的影响。有一些变更不严重，比如我们在返回结果中增加了新的字段，现有的客户端应用程序可能会继续正常工作（如果能够忽略无法识别的字段），而新的客户端应用程序则可以设计为处理该新字段。这种情况下可以不做版本控制。但是有些更改可能是重大的，造成现有客户端无法正常工作，需要做版本控制。

最常用的方法是在 URI 中进行版本控制，资源通过带版本号的 URI 公开，例如：

```
/v2/customers/3
```

## 接口文档

文档是件头疼的事，文档不仅难于撰写，而且难于维护。因为需求与代码会经常变动，必须付出巨大的精力保持文档与代码的同步。示例代码集成了 `Swagger`. `Swagger` 有各种语言的插件，可以通过配置及少量代码，生成接口文档及测试界面。

集成方式，在项目中引入 `Swagger` 相关的依赖：

```xml
<dependency>
	<groupId>io.springfox</groupId>
	<artifactId>springfox-swagger2</artifactId>
	<version>2.2.2</version>
</dependency>
<dependency>
	<groupId>io.springfox</groupId>
	<artifactId>springfox-swagger-ui</artifactId>
	<version>2.2.2</version>
</dependency>
```

创建相关的 `Bean`：

```java
@Configuration
@EnableSwagger2
public class Swagger2 {

	@Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.mydomain"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Spring Boot API Doc Sample")
                .description("Hello world!")
                .build();
    }
    
}
```

现在就可以生成基本的文档了，可以访问 `http://localhost:8080/swagger-ui.html` 查看 API Doc，并且可以对接口进行测试调用。

可以在代码中添加一些标注，提高文档的可读性，比如：`@Api`, `@ApiOperation`, `@ApiModel`, `@ApiModelProperty`. 
