package com.lu.dfw.demo;

import com.google.protobuf.InvalidProtocolBufferException;
import com.lu.dfw.proto.Login;


public class test {
    public static void main(String[] args) throws InvalidProtocolBufferException {
        Login.UserRegisterRequest.Builder builder = Login.UserRegisterRequest.newBuilder();
        Login.UserRegisterRequest build = builder.setUsername("123").setPassword("456").build();
        byte[] byteArray = build.toByteArray();
        System.out.println(byteArray.length);

        Login.UserRegisterRequest userRegisterRequest = Login.UserRegisterRequest.parseFrom(byteArray);
        System.out.println(userRegisterRequest);
    }
}
