package com.example.test;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Result extends AppCompatActivity {

    String fileName = "";
    String filePath = "";
    Interpreter tflite;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

        Intent intent = getIntent();
        String str = intent.getStringExtra("message");

        ListView listView = findViewById(R.id.list_view);
//        TextView textValue = findViewById(R.id.Text);
        fileName = "DataFile.txt";
        filePath = "MyFileDir";

        List<String> list = new ArrayList<>();

        try {
            PackageInfo p = getPackageManager().getPackageInfo(str, PackageManager.GET_PERMISSIONS);
            for (int i = 0; i < p.requestedPermissions.length; i++) {
                list.add(p.requestedPermissions[i]);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

//        if(!isExternalStorageAvailableForRW()){
//            Toast.makeText(getApplicationContext(),"!!!External Storage Not Available!!!",Toast.LENGTH_SHORT).show();
//        }
//        else{
//            fileContent = new Gson().toJson(list);
//            if(!fileContent.equals("")){
//                File myExternalFile = new File(getExternalFilesDir(filePath), fileName);
//                FileOutputStream fos = null;
//                try {
//                    fos = new FileOutputStream(myExternalFile);
//                    fos.write(fileContent.getBytes());
//                    fos.close();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                Toast.makeText(getApplicationContext(),"!!!Data saved to external file!!!",Toast.LENGTH_SHORT).show();
//            }
//        }

        List<String> list1 = readAssets();
        try {
            float[][] result = predict(list,list1);
//            List<float[]> list3 =  Arrays.asList(result);
////            textValue.setText(result);
//            ArrayAdapter adapter=new ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line, list3);
//            listView.setAdapter(adapter);
        } catch (IOException e) {
            e.printStackTrace();
        }








//        File myObj = new File("C:\\Users\\5570\\IdeaProjects\\test\\app\\src\\main\\res\\values\\newFile.json");
//        FileWriter myWriter = null;
//        try {
//            myWriter = new FileWriter("C:\\Users\\5570\\IdeaProjects\\test\\app\\src\\main\\res\\values\\newFile.json");
//
//            myWriter.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }




    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private float[][] predict(List<String> list, List<String> list1) throws IOException {
        List<String> myList = new ArrayList<String>();
        float[][] array = new float[1][215];

        for(int i=0;i<215;i++){
            array[0][i] = (float)0;
        }

        for(String var : list){
            if(checkString(var)!=""){
                myList.add(checkString(var));
            }
        }
        myList.replaceAll(String::toLowerCase);
        list1.replaceAll(String::toLowerCase);

        for (String var : myList){
            if(list1.contains(var)){
                int retval=list1.indexOf(var);
                array[0][retval] = (float)1;
            }
        }

        try {
            tflite = new Interpreter(loadModelFile());
        }catch (Exception ex){
            ex.printStackTrace();
        }
        float[][] prediction=doInference(array);



//        try {
//            MobileModel model = MobileModel.newInstance(getApplicationContext());
//
//            // Creates inputs for reference.
//            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 1, 215}, DataType.FLOAT32);
//            byte[] bytes = floatsToBytes(array);
//            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
//
//            inputFeature0.loadBuffer(byteBuffer);
//
//            // Runs model inference and gets result.
//            MobileModel.Outputs outputs = model.process(inputFeature0);
//            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
//
//            float[] data=outputFeature0.getFloatArray();
//
//
//            // Releases model resources if no longer used.
//            model.close();
//            return data[0];
//        } catch (IOException e) {
//            // TODO Handle the exception
//        }
        return prediction;
    }


    private MappedByteBuffer loadModelFile() throws IOException, PackageManager.NameNotFoundException {
        Context context = createPackageContext("com.example.test", 0);
        AssetManager assetManager = context.getAssets();
        AssetFileDescriptor fileDescriptor=assetManager.openFd("mobile_model.tflite");
        FileInputStream inputStream=new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();
        long startOffset=fileDescriptor.getStartOffset();
        long declareLength=fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,declareLength);
    }

    private float[][] doInference(float[][] inputArray) {
        float[][] output=new float[1][1];
        tflite.run(inputArray,output);
        return output;
    }








//    public static byte[] floatsToBytes(float[] floats) {
//        byte bytes[] = new byte[Float.BYTES * floats.length];
//        ByteBuffer.wrap(bytes).asFloatBuffer().put(floats);
//        return bytes;
//    }

    private static String checkString(String str) {
        char ch;
        String result="";
        for(int i=0;i < str.length();i++) {
            ch = str.charAt(i);

            if (Character.isUpperCase(ch)) {
                result+=ch;
            }
        }

        return result;
    }

    private List<String> readAssets(){
        InputStream is = null;
        String text = "";
        try {
            is = getAssets().open("data.txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            text = new String(buffer);
//            List<String> participantJsonList = mapper.readValue(text, new TypeReference<List<String>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        //            List<String> users = new Gson().fromJson(new FileReader("lable.txt"),
//                    new TypeToken<List<String>>() {}.getType());

        List<String> users = Arrays.asList(text.split("\n"));
        return users;
    }

    private boolean isExternalStorageAvailableForRW() {
        String extStorageState = Environment.getExternalStorageState();
        if(extStorageState.equals(Environment.MEDIA_MOUNTED))
            return true;
        return false;
    }
}
