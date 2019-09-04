package com.jacky.rpc.server;

import lombok.Data;

import java.io.Serializable;

/**
 * 传输DTO包装
 *
 * @author Jacky
 * @date 2019/9/4 3:15 PM
 */
@Data
public class RpcTransformDto implements Serializable {
    private static final long serialVersionUID = -604547811922204211L;

    private String fullClazzPath;
    private String methodName;
    private Object[] params;
}
