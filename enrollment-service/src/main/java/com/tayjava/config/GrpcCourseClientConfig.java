package com.tayjava.config;

import com.tayjava.common.grpc.course.CourseInternalGrpc;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.ImportGrpcClients;

@Configuration
@ConditionalOnProperty(
    prefix = "tayjava.clients",
    name = "course-transport",
    havingValue = "grpc",
    matchIfMissing = true)
@ImportGrpcClients(target = "course", types = CourseInternalGrpc.CourseInternalBlockingStub.class)
public class GrpcCourseClientConfig {}
