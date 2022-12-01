import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import json.Prediction;
import json.Root;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


public class InferenceLocal {
    public static void main(String[] args) throws IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        // Get json.Image Path
        String filePath = "src/main/resources/img/image-picker4722773875026562-heic.jpg";
        File file = new File(filePath);
Mat mat= Imgcodecs.imread(filePath);
        // Base 64 Encode
        String encodedFile;
        FileInputStream fileInputStreamReader = new FileInputStream(file);
        byte[] bytes = new byte[(int) file.length()];
        fileInputStreamReader.read(bytes);
        encodedFile = new String(Base64.getEncoder().encode(bytes), StandardCharsets.US_ASCII);

       /* String API_KEY = "iJtaImp5GrBtHzffbVzg"; // Your API Key
        String DATASET_NAME = "nn-gl4tb"; //
        String MODEL_ENDPOINT = "nn-gl4tb/3"; // model endpoint*/

        String API_KEY = "B1Tbf9LNtzt7CLZxfePX"; // Your API Key
        String DATASET_NAME = "perfilssf"; // Set Dataset Name (Found in Dataset URL)
        String MODEL_ENDPOINT = "perfilssf/2"; // model endpoint
        // Construct the URL
        String uploadURL = "https://detect.roboflow.com/" + MODEL_ENDPOINT + "?api_key=" + API_KEY
                + "&classes=perfil&confidence=70&overlap=80";

        // Http Request
        HttpURLConnection connection = null;
        try {
            // Configure connection to URL
            URL url = new URL(uploadURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length", Integer.toString(encodedFile.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setUseCaches(false);
            connection.setDoOutput(true);

            // Send request
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(encodedFile);
            wr.close();

            // Get Response
            InputStream stream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            StringBuilder json = new StringBuilder("");
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            System.out.println(json);
            Gson gson=new GsonBuilder().create();
 Root root=gson.fromJson(json.toString(),Root.class);

            System.out.println(root.predictions.get(0).myclass);

            for (Prediction prediction: root.predictions) {

                Imgproc.rectangle(mat,new Point((int) prediction.x, (int) prediction.y),
                        new Point((int)prediction.x+prediction.width, (int)prediction.y+prediction.height),

                        new Scalar(0,255,255),2);

            }
          Imgproc.putText(mat, ""+root.predictions.size(),
                  new Point(mat.cols() /2 , mat.rows()/25 ),
                  3, 5, new Scalar(0, 255, 255));
            Configs.convertMatToImage(mat);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

    }

}
