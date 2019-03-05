package com.cmx.shiroweb.chat.component.message.handler;

import com.cmx.shiroweb.chat.enums.MessageType;
import com.cmx.shiroweb.chat.proto.ChatMessageOuterClass;
import com.google.protobuf.ByteString;
import com.googlecode.protobuf.format.JsonFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

/**
 * @author cmx
 * @date 2019/3/1
 */
@Slf4j
@Component
public class FileMessageHandler extends ProtoBufMessageHandler {

    private static final String UPLOAD_FILE_PATH = "F:\\upload\\";

    @Override
    public void handleMessage(ChatMessageOuterClass.ChatMessage chatMessage) {
        if(MessageType.FILE.getCode() == chatMessage.getMessageType()){
            ChatMessageOuterClass.ChatMessage.FileMessage fileMessage = chatMessage.getFileMessage();
            if(fileMessage == null){
                log.error("file handler get null file message with wrong message type");
                return;
            }
            log.info("file handler parse file message: {}", chatMessage.getFileMessage().getFileName());
            //将文件上传到指定位置， 上传完成之后将文件保存路径放入消息体中返回， 点击下载.
            String fileName = fileMessage.getFileName();
            ByteString fileContent = fileMessage.getFileContent();
            byte[] bytes = fileContent.toByteArray();
            //上传需要生成唯一的名称，防止覆盖，只保留后缀
            String uploadFileName = generateFileName(fileName);
            File f = new File(uploadFileName);
            saveFile(bytes, f);
            ChatMessageOuterClass.ChatMessage message = chatMessage.
                    toBuilder().
                    setMessageContext(fileName).
                    clearFileMessage().
                    setFilePath(uploadFileName).
                    build();
            //消息路由
            messageRouter.route(message);
            //保存消息
            saveMessage(message);
        } else {
            nextHandler.handleMessage(chatMessage);
        }
    }


    private void saveFile(byte[] data, File f) {
        FileOutputStream fos = null;
        try {
            fos  = new FileOutputStream(f);
            fos.write(data);
            fos.flush();
        }catch(Exception e){
            log.error("open File get error : {}", e);
        }finally {
            if(fos != null) {
                try {
                    fos.close();
                }catch(Exception e){
                    log.error("close fileOutputStream get error : {}", e);
                }
            }
        }
    }

    private String generateFileName(String fileName) {
        UUID uuid = UUID.randomUUID();
        String suffix;
        if(!fileName.contains(".")){
            suffix = ".zip";
        }
        String[] split = fileName.split("\\.");
        //不考虑文件夹， 文件夹需要压缩
        suffix = "." + split[split.length-1];
        return UPLOAD_FILE_PATH + uuid.toString().replace("-", "").concat(suffix);
    }
}
