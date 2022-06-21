## SpringBoot中的Dubbo和Zookeeper整合以及消费者与提供者案例

1.首先我们需要在电脑上安装Zookeeper，并且启动，需要注意的是zookeeper服务端启动需要一个配置文件zoo.cfg，我们需要复制zoo_sample.cfg这个文件然后将其重命名为zoo.cfg，然后运行bin目录下的zkService.cmd,与zkCli.cmd文件测试是否能链接成功。

2.测试链接成功后，我们在springboot环境下编写消费者提供者案例来模拟分布式应用，首先我们需要编写两个springboot模块，一个作为消费者一个作为提供者。consumer-service，provider-service

* 2.1 我们先创建一个springboot项目命名为 provider-service，然后引入对应的maven依赖具体如下

```xml
 <!-- Dubbo Spring Boot Starter -->
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo-spring-boot-starter</artifactId>
            <version>2.7.3</version>
        </dependency>
        <!-- https://mvnrepository.com/artifact/com.github.sgroschupf/zkclient -->
        <dependency>
            <groupId>com.github.sgroschupf</groupId>
            <artifactId>zkclient</artifactId>
            <version>0.1</version>
        </dependency>
        <!-- 引入zookeeper -->
        <dependency>
            <groupId>org.apache.curator</groupId>
            <artifactId>curator-framework</artifactId>
            <version>2.12.0</version>
        </dependency>
        <dependency>
            <groupId>org.apache.curator</groupId>
            <artifactId>curator-recipes</artifactId>
            <version>2.12.0</version>
        </dependency>
        <dependency>
            <groupId>org.apache.zookeeper</groupId>
            <artifactId>zookeeper</artifactId>
            <version>3.4.14</version>
            <!--排除这个slf4j-log4j12-->
            <exclusions>
                <exclusion>
                    <groupId>org.slf4j</groupId>
                    <artifactId>slf4j-log4j12</artifactId>
                </exclusion>
            </exclusions>
        </dependency>

```

* 2.2 然后我们需要修改springboot配置文件，先是要修改服务端口，然后是要指明zookeeper的地址与端口，最后是指明自己这个服务的名称，以及本地service的包扫描，方便消费者进行调用。

```properties
# 服务的端口号
server.port=8081
# 应用名称
spring.application.name=provider-server
# zookeeper的地址
dubbo.registry.address=zookeeper://127.0.0.1:2181
#扫描指定包下服务
dubbo.scan.base-packages=com.czy.service

```

* 2.3 然后我们需要编写提供者的服务，创建包与上面的文件包扫描路径保持一致，然后在里面编写接口，编写实现类提供相应的服务，速需要注意的是在这里的@service注解是dubbo中的service并不是spring中的service。

```java
package com.czy.service;
/**
 * @author Planifolia.Van
 * 模拟分布式购票系统的Service
 */
public interface TicketService {
    /**
     * 购票接口
     * @return arg
     */
    String getTicket();
}
```

```java
package com.czy.service;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;
import java.util.Random;

/**
 * @author Planifolia.Van
 */
@Component
@Service
public class TicketServiceImpl implements TicketService{
    @Override
    public String getTicket() {
        Random random=new Random();
        return "拿到了第"+random.nextInt(100)+"张票";
    }
}

```

* 2.4 编写消费者模块模拟去执行提供者模块中的方法，同样对于消费者模块我们仍然需要创建一个新的springboot模块，然后引入maven依赖，修改配置文件。在配置文件中我们需要修改端口号，以免与服务端的端口号重复，指明zookeeper的地址与端口，

```xml
  <!--zookeeper-->
        <!-- https://mvnrepository.com/artifact/com.github.sgroschupf/zkclient -->
        <dependency>
            <groupId>com.github.sgroschupf</groupId>
            <artifactId>zkclient</artifactId>
            <version>0.1</version>
        </dependency>
        <!-- 引入zookeeper -->
        <dependency>
            <groupId>org.apache.curator</groupId>
            <artifactId>curator-framework</artifactId>
            <version>2.12.0</version>
        </dependency>
        <dependency>
            <groupId>org.apache.curator</groupId>
            <artifactId>curator-recipes</artifactId>
            <version>2.12.0</version>
        </dependency>
        <dependency>
            <groupId>org.apache.zookeeper</groupId>
            <artifactId>zookeeper</artifactId>
            <version>3.4.14</version>
            <!--排除这个slf4j-log4j12-->
            <exclusions>
                <exclusion>
                    <groupId>org.slf4j</groupId>
                    <artifactId>slf4j-log4j12</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
```

```properties
# 端口号
server.port=8080
# 应用名称
spring.application.name=consumer-server
#注册中心地址
dubbo.registry.address=zookeeper://127.0.0.1:2181


```

* 2.5 配置文件相关的配置好之后我们就要开始编写消费相关的类了，需要注意的是在消费者中引入生产者中的方法，需要在模块中创建一个与消费者方法相同全限制名的接口（接口名与报名完全一致）然后在编写用户服务，UserService在里面引入远程模块中的服务然后调用。需要注意的是，在消费者服务中同样要添加注解@Component与@Service这个service同样也是dubbo中的service

```java
package com.czy.service;

/**
 * @author Planifolia.Van
 */
public interface TicketService {
    public String getTicket();
}

```

```java
package com.czy.service;

import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;

/**
 * @author Planifolia.Van
 * 消费者的服务类，在这他远程的调用生产者里的getTicket方法
 */
@Component
@Service
public class UserService {
    /**
     * 注意使用@Reference将远程的服务引入，然后我们需要要在远程服务相同的包下建一个对应的接口，不需要实现类
     */
    @Reference
    TicketService ticketService;
    public void getTicket(){
        System.out.println(ticketService.getTicket());
    }
}

```

3.运行测试，首先需要启动我们最开始安装的zookeeper服务器，这个服务器的核心就是用来做中介的，我们服务者创建的服务会注册到zookeeper服务器中，然后消费者启动之后会找到我们在配置文件中指定的zookeeper服务器的地址然后在这里面取到我们在客户端中指明的那个服务，由于我们这是在本地进行演示，zookeeper服务器，消费者提供者都在一台电脑上，如果我们在实际开发中，zookeeper服务器，消费者，提供者都可以放在不同的服务器上，只需要在配置文件中指明对应的服务器地址就可以了。
