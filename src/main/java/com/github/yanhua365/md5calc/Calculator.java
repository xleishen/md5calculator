package com.github.yanhua365.md5calc;

import com.sun.javafx.binding.StringFormatter;

import javax.swing.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件或目录的md5吗计算器
 */
public class Calculator {
    public static final String JSON_FORMAT = "JSON";
    public static final String XML_FORMAT = "XML";
    private JTextField txtFilePath;
    private JButton btnSelectFile;
    private JTextArea txtResult;
    private JPanel rootPanel;
    private JButton btnCalculate;
    private JRadioButton rdoJsonFormat;
    private JRadioButton rdoXmlFormat;

    final JFileChooser fileChooser;//文件选择器

    private final ModelBean model;

    public Calculator() {

        model = new ModelBean();
        model.setOutFormat(JSON_FORMAT);

        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        ButtonGroup group = new ButtonGroup();
        group.add(rdoJsonFormat);
        group.add(rdoXmlFormat);

        rdoJsonFormat.setSelected(true);


        btnSelectFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int chooserResult = fileChooser.showOpenDialog(null);
                if(chooserResult ==JFileChooser.APPROVE_OPTION ){
                    model.setFilePath(fileChooser.getSelectedFile().getAbsolutePath());
                    setData(model);
                }
            }
        });
        btnCalculate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(model.getFilePath() == null || "".equals(model.getFilePath())){
                    return;
                }
                File selectedFile = new File(model.getFilePath());
                List<FileInfoBean> fileInfoBeans = selectedFile.isDirectory()?calcDir(selectedFile):calcFile(selectedFile);
                if(rdoXmlFormat.isSelected()){
                    model.setResult(coverToXml(fileInfoBeans));
                }else if(rdoJsonFormat.isSelected()){
                    model.setResult(coverToJson(fileInfoBeans));
                }

                setData(model);
            }
        });
    }

    private String coverToXml(List<FileInfoBean> fileInfoBeanList) {
        FilesInfoBean filesInfoBean = new FilesInfoBean();
        filesInfoBean.setFiles(fileInfoBeanList);
        StringWriter writer = new StringWriter();
        try {
            JAXBContext context = JAXBContext.newInstance(filesInfoBean.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(filesInfoBean, writer);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    private String coverToJson(List<FileInfoBean> fileInfoBeanList) {
        StringBuilder result = new StringBuilder();
        for(FileInfoBean file : fileInfoBeanList){
            if(result.length()>0)
                result.append(",\n");

            String tpl = "  {\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\"}";
            result.append(String.format(tpl,"url",file.getUrl(),"md5",file.getMd5(),"size",file.getSize()));
        }

        return "[\n"+result.toString()+"\n]";
    }

    private List<FileInfoBean> calcFile(File file) {
        List<FileInfoBean> files = new ArrayList<FileInfoBean>();
        files.add(calc(file));
        return files;
    }

    private List<FileInfoBean> calcDir(File dir) {
        List<FileInfoBean> files = new ArrayList<FileInfoBean>();
        recurseCalc(dir, files);
        return files;
    }

    private void recurseCalc(File dir, List<FileInfoBean> files) {
        for(File f : dir.listFiles()){
            if(f.isFile()){
                FileInfoBean finfo = calc(f);
                files.add(finfo);
            }else {
                recurseCalc(f, files);
            }
        }
    }

    /**
     * 计算文件的 MD5 ，url和大小
     * @param f
     * @return
     */
    private FileInfoBean calc(File f) {
        FileInfoBean result = new FileInfoBean();
        try {
            FileInputStream fis = new FileInputStream(f);
            String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
            result.setMd5(md5);
        } catch (IOException e) {
            e.printStackTrace();
        }

        result.setSize(f.length()+"");
        String url = null;
        if(model.getFilePath().equals(f.getAbsolutePath())){//文件
            url = "./" + f.getAbsolutePath().substring(f.getAbsolutePath().lastIndexOf(File.separator)+1);
        }else{
            url = "./" + f.getAbsolutePath().substring(model.getFilePath().length()+1);
        }

        url = url.replace(File.separator,"/");


        result.setUrl(url);
        return result;
    }


    public void setData(ModelBean data) {
        txtFilePath.setText(data.getFilePath());
        txtResult.setText(data.getResult());
    }

    public void getData(ModelBean data) {
        data.setFilePath(txtFilePath.getText());
        data.setResult(txtResult.getText());
    }

    public boolean isModified(ModelBean data) {
        if (txtFilePath.getText() != null ? !txtFilePath.getText().equals(data.getFilePath()) : data.getFilePath() != null)
            return true;
        if (txtResult.getText() != null ? !txtResult.getText().equals(data.getResult()) : data.getResult() != null)
            return true;
        return false;
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("Md5Calculator - ");
        frame.setContentPane(new Calculator().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(750, 550);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
}
