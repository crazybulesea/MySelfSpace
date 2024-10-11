package com.lu.dfw.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

import java.util.List;

public class MyDecoder extends ByteToMessageDecoder {
    // 自定义实现,没有采用 varint32
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // 记录当前位置
        in.markReaderIndex();
        // 读当前标记
        int preIndex = in.readerIndex();
        // 尝试读取 本次消息的长度,但是不一定能读取到
        int length = readInt(in);
        // 再次读 当前标记,判断是否改变,没有变化 说明 没有读取 消息长度,直接 结束
        if (preIndex == in.readerIndex()) {
            return;
        }
        // 读取到了不可能存在的 负数
        if (length < 0) {
            throw new CorruptedFrameException("negative length: " + length);
        }

        // 如果消息长度 不足 包头声明的长度,将游标重置
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
        } else {
            // 将数据读入 到body中
            byte[] body = new byte[length];
            in.readBytes(body);
            out.add(body);
        }
    }


    /**
     * 自定义实现,没有采用 varint32
     *
     * @param buffer
     * @return
     */
    private static int readInt(ByteBuf buffer) {
        // 不可读
        if (!buffer.isReadable()) {
            return 0;
        }

        // 如果可读 字节 不足 int,返回0
        if (buffer.readableBytes() < 4) {
            return 0;
        }

        return buffer.readInt();
    }
}
