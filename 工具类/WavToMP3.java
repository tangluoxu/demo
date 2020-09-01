package com.eshore.hb.btsp114busiservice.product.util;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncodingAttributes;

import java.io.File;

/**
 * @author tlj
 * @title: WavToMP3
 * @projectName btsp114
 * @description: TODO
 * @date 2020/9/117:14
 */
public class WavToMP3 {

        /**
         * 执行转化过程
         *
         * @param source
         *            源文件
         * @param desFileName
         *            目标文件名
         * @return 转化后的文件
         */
        public static File execute(File source, String desFileName)
                throws Exception {
            File target = new File(desFileName);

            AudioAttributes audio = new AudioAttributes();
            audio.setCodec("libmp3lame");
            audio.setBitRate(new Integer(128000));
            audio.setChannels(new Integer(2));
            audio.setSamplingRate(new Integer(44100));
            EncodingAttributes attrs = new EncodingAttributes();
            attrs.setFormat("mp3");
            attrs.setAudioAttributes(audio);
            Encoder encoder = new Encoder();
            encoder.encode(source, target, attrs);

            return target;
        }
}
