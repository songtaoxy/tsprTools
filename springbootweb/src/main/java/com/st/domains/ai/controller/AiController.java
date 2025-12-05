package com.st.domains.ai.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.st.common.pojo.others.Person;
import com.st.domains.ai.service.impl.EmbeddingService;
import com.st.infrastructure.client.http.webclient.EmbeddingResponse;
import com.st.infrastructure.client.http.webclient.old.PythonServiceClient;
import com.st.modules.demo.model.entity.User2;
import com.st.common.response.Response;
import com.st.common.response.Result;
import com.st.modules.demo.service.User2Service;
import com.st.modules.demo.service.HelloService;
import com.st.modules.demo.service.UserService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/ai")
public class AiController {
    static String template;

    @Autowired HelloService helloService;
    @Autowired UserService userService;


    @Autowired
    User2Service user2Service;

    @Autowired
    private EmbeddingService embeddingService;

    Person p = null;

    @Autowired
    private PythonServiceClient pythonServiceClient;

    @SneakyThrows
    @GetMapping(value = "/chat")
//    public  Response<Result> helloWorld(@RequestParam Map map) {
        public  Response<Result> helloWorld() {

        Map<String, Object> hello = new HashMap<>();
        hello.put("hello", "helloworld");
        hello.put("hello2", "helloworld");

        // call python
        EmbeddingResponse embeddingResponse = embeddingService.doEmbedding("hi, ai");


        System.out.println("controller:"+hello);

        Page<User2> user2Page = new Page<>(0, 2);
        QueryWrapper<User2> user2QueryWrapper = new QueryWrapper<User2>();

        user2QueryWrapper.gt("u.id", 1);
        IPage<Map<String,Object>> stringIPage = user2Service.queryByPage(user2Page, user2QueryWrapper,"null");
        System.out.println(stringIPage.getTotal());
        System.out.println(stringIPage.getPages());
        List<Map<String,Object>> records = stringIPage.getRecords();
        records.forEach(System.out::println);

        Result build = Result.build(hello.toString());

        return Response.ok(build);
    }

}



