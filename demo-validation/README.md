# demo-validation
A Applacation for Test Spring-Validation


从 JSR 303 开始，Bean Validation 已经成为 JAVA 的标准规范之一。到目前为止，Bean Validation 已经经过三个版本的迭代--JSR 303(1.0)、JSR 349(1.1)、JSR 380(2.0)。
Hibernate Validator 又对JSR 303 进行了扩展，合理运用这两个框架提供的校验功能可以为项目省去很多重复的校验代码。
Spring 已经为数据校验提供了很好的支持，基于 Spring 和 Bean Validation 规范中的一些注解，再加上一些自定义内容，能极大简化项目中参数校验代码。

[Bean Validation 官网地址](https://beanvalidation.org)

[Hivernate Validator 文档地址](https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/)

本文只讨论 Bean Validation 的常用姿势，足够解决项目中90%的校验需求，如需彻底了解 Bean Validation，还需认真阅读上面的两个文档。
*本文所有的测试代码已上传gitHub，项目地址：[demo-validation](https://github.com/yueyakun2017/demo-validation)*

## 目录

* TOC
{:toc}

## Bean Validation

实现校验最主要的两个组件是约束注解（Constraint annotation）和约束校验器（Constraint validator）。校验注解加在需要校验的参数或者其内部属性上，校验器负责判断该参数或其内部属性是否符合检验规则。

### 约束注解（Constraint annotation）

> 如果注解的保持策略包含 RUNTIME，并且注释本身是用 javax.validation.Constraint 注释的，则该注释被认为是约束注解。-- JSR 303 文档

#### 官方内置的约束注解

[官方内置注解文档地址](https://beanvalidation.org/2.0/spec/#builtinconstraints)
内置的约束注解都在 javax.validation.constraints 包，Hibernater 的约束注解在 org.hibernate.validator.constraints 包。官方包里的约束注解比较多，不一一列举了，这些约束注解基本上能满足项目中 80% 以上的校验需求。Spring 默认引入了这两个包，所以我们在使用的时候基本不用再手动引入。

#### 约束注解的默认字段

约束注解有三个保留名称字段，这三个字段有特殊意义，每个约束注解都必须有

* message
    * 每一个约束注解都必须有一个 String 类型的 message 字段
    * 这个属性的值是用来构建校验失败时的报错信息的
    * 值可以直接是错误信息本身，也可以是一个占位符，真正的错误信息放在 ValidationMessages.properties 文件中

* group
    * 每一个约束注解都必须有一个 Class 数组类型的 group 字段
    * group 字段的默认值必须是空数组
    * 作用是控制校验顺序或者执行部分校验

* payload
    *  每一个约束注解都必须有一个 Payload 数组类型的 payload 字段
    * payload 字段的默认值必须是空数组
    * 通常用它控制校验失败时的日志等级

* 其他自定义属性
    * 根据约束的不同，约束注解还会定义其他类型的字段，如 @Size 注解中的 min 和 max 字段

#### 校验组

group 属性相同的字段属于同一校验组。group 属性可以是数组类型，所以一个字段有可能属于多个校验组。校验组有以下两个功能：

* 实现部分校验
约束注解是可以通过指定 group 来实现分组校验的，比如在项目中，一条数据“添加”和“修改”的校验规则肯定是不一样的，这时我们可以指定对象中字段约束注解的校验组来实现部分校验

* 控制检验顺序
控制校验顺序需要配合 @GroupSequence 注解使用，定义如下一个校验组接口，就可以实现 先校验 New.class 组的约束条件后校验 Custom.class 组的约束条件。（**注意：如果前面组的校验不通过，后面组的约束校验是不会进行的**）

```java
import javax.validation.GroupSequence;

@GroupSequence({New.class,Update.class})
public interface NewAndUpdate {

}
```
*用group实现部分校验的测试在 [demo-validation](https://github.com/yueyakun2017/demo-validation) 项目的 TestCustomer 类中*

#### 校验的继承

父类中的校验注解在子类中是一样生效的

#### 级联校验
Bean Validation 支持级联校验（文档没说最多关联几级）
有 A 和 B 两个 JavaBean，如果 A 中有一个 B 类型的字段 b，校验 A 的时候想延伸校验段 b，只需在 b 字段上加 @Valid 注解。

级联校验也适用于集合类型的字段，如：
* 集合
* 实现了java.lang.Iterable接口( 例如Collection, List 和 Set)
* 实现了java.util.Map接口

*关于级联校验的简单测试在 [demo-validation](https://github.com/yueyakun2017/demo-validation) 项目的 TestCustomer 类中*

#### 约束条件组合

约束注解是可以组合使用的。比如可以在自定义注解上添加一个或多个定义好约束注解，这样这个自定义注解就同时有了多个约束条件，比如下面的这个自定义的 @KeyId 注解。它上面标注了 @Min 和 @NotNull 注解，这样 @KeyId 就同时拥有了
@Min 和 @NotNull 的约束条件，当然你还可以用 @Constraint 注解再指定一个校验器。其中 @ReportAsSingleViolation 注解的作用是不管当违反哪个约束，都报本注解的 message 信息，不加这个注解的话会报各自注解的 message 信息。
```java
//校验是否符合主键id要求，不为null，大于等1
@Min(1)
@NotNull
@ReportAsSingleViolation
@Constraint(validatedBy = {})
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.METHOD,ElementType.PARAMETER,ElementType.ANNOTATION_TYPE})
public @interface KeyId {
    String message() default "{custom.constraints.KeyId.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```
*关于约束条件组合的简单测试在 [demo-validation](https://github.com/yueyakun2017/demo-validation) 项目的 TestCustomer 类中*

#### 可重复约束注解

Bean Validation 2.0 的一个新特性就是所有的内置注解都修改为了可重复注解，可重复注解的使用比较简单，举例如下。 

```java
public class Customer{
	private Integer id;
	
	@Size.List({
    		@Size(min=8,max=20,message = "普通用户昵称长度最少为8，最多为20"),
    		@Size(min=4,max=30,groups = {VIP.class},message = "VIP用户昵称长度最少为4，最多为30")})
    private String nickname;
}
```

#### 自定义约束注解

示例项目 demo-validation 中的 @CheckCase 就是一个简单的自定义注解。它的作用是校验目标字符串都为 
大写或者小写（通过 mode 属性指定）。
@CheckCase 的校验器是 CheckCaseValidator 类。校验的判断逻辑都在这个类里。

#### 错误消息提示

错误消息提示信息可以在使用约束注解是通过 message 属性指定，也可以使用表达式配合在 resources 目录下的 ValidationMessages.properties 文件来指定。

通过配置文件指定的时候支持通配符匹配，框架会自动匹配约束注解中属性名字相同的通配符。如果是其他特殊名称的通配符，也可以在验证器的 isValid 方法中通过向 ConstraintValidatorContext 上下文中增加参数，从而实信息填充。如 @CheckCase 的检验提示信息就采用了通配符的方式。
 
### 约束校验器（Constraint validator）
 
 还是以前面自定义的@CheckCase 和 CheckCaseValidator 类为例。这里说一下约束校验器，约束校验器主要提供判断被校验对象是否满足约束条件的代码逻辑。校验器必须实现 ConstraintValidator 接口。
 它有两个泛型参数：
* 第一个是这个校验器所服务的约束注解类型（在我们的例子中即CheckCase）
* 第二个这个校验器所支持到被校验元素到类型（即String）。
 
 CheckCaseValidator 类有两个方法：
* 方法 initialize 默认是一个空方法，入参是约束注解实例，子类通过重新这个方法可以完成一项校验的初始化工作。例子中在这一步做了初始化 caseMode 字段的工作。
* 方法 isValid 返回一个 boolean 值，校验通过返回 true 校验失败返回 false。入参是目标字段的 value 和 ConstraintValidatorContext ，value 用于检验逻辑，通过 ConstraintValidatorContext 参数可以实现对报错信息的动态修改。

## Spring 对 Bean Validation 的支持

如果每当校验约束的时候都要创建一个 Validator，然后调用 Validator 的 validate 方法，之后再出来返回的校验结果也挺烦的。还好 Spring 为 Bean Validation 提供了很好的支持。

Spring 的参数校验功能有两部分组成：
1. 首先是 argument resolver 内部进行的 data-binding 时的 参数校验（validation），这个阶段的 validation 是由spring-mvc实现并决定的。

2. 第二部分validation是由spring-framework，基于aop实现的。

下面对这两部分校验功能进行详细说明

### data-binding 时的参数校验

这一层的参数校验可以看做是由 Spring MVC 在 argument resolver 阶段进行 data-binding 时顺便进行的，这个阶段的参数校验有以下几个特点：
* 只在 Controller 层生效
* 必须在 Controller 上添加 @Validated 注解
* RequestParam 参数校验异常会直接返回 500 错误
* RequestBody 对象参数必须在对象参数前加@Valid 或 @Validated 注解才会触发内部属性的校验规则
* RequestBody 参数校验异常结果可以用 BindingResult 对象接收，然后自主处理，也可以不接收。不接收会直接 400 Bad Request

*这部分的测试代码主要在[demo-validation](https://github.com/yueyakun2017/demo-validation)项目的 AddressController 类中*

### Spring 基于 AOP 实现的参数校验
这部分的参数校验由 spring-framework 基于 AOP 实现，特点如下：
* 只要一个类被注册为bean并且加了@Validated注解，则spring为此类的所有方法提供参数校验
* 对象参数必须在对象参数前加@Valid 注解才会触发内部属性的校验规则
* 校验不通过会报异常，异常类型为 javax.validation.ConstraintViolationException
* 由于基于AOP实现，所以类内部调用是不会触发validation的

*这部分的测试代码主要在[demo-validation](https://github.com/yueyakun2017/demo-validation)项目的 TestAOPValidationController 和 AddressService 类中*


## Bean Validation 2.0 的新特性

1. 对 Java 的最低版本要求是 Java 8

2. 支持容器的校验，通过TYPE_USE类型的注解实现对容器内容的约束：List<@Email String>

3. 支持日期时间类型校验，@Past 和 @Future

4. 新增内置约束注解，@Email, @NotEmpty, @NotBlank, @Positive, @PositiveOrZero, @Negative, @NegativeOrZero, @PastOrPresent and @FutureOrPresent

5. 所有内置注解都是可重复注解

