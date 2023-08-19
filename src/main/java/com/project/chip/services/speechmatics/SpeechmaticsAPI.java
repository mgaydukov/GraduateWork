package com.project.chip.services.speechmatics;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class SpeechmaticsAPI {
    final String apiUrl = "https://asr.api.speechmatics.com/v2/jobs/";
    final String apiToken = "aJz94J1ihPcFzSSh4DioGPj3mzgAkV5t";
    String youtubeUrl;

    public SpeechmaticsAPI(){
    }

    public SpeechmaticsAPI(String youtubeUrl){
        this.youtubeUrl = youtubeUrl;
    }

    public String transcribe() {

        try {
            // Загрузка аудио из YouTube
            File audioFile = downloadAudioFromYouTube(youtubeUrl);

            if (audioFile != null) {
                try {
                    URL url = new URL(apiUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Authorization", "Bearer " + apiToken);
                    connection.setRequestProperty("Content-Type", "multipart/form-data");
                    connection.setDoOutput(true);

                    String boundary = "Boundary-" + System.currentTimeMillis();
                    connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                    try (OutputStream outputStream = connection.getOutputStream()) {
                        // Add the "config" field
                        writeFormField(outputStream, boundary, "config", "{\"type\": \"transcription\",\"transcription_config\": { \"operating_point\":\"enhanced\", \"language\": \"ru\" }}");

                        // Add the "model" field
                        writeFormField(outputStream, boundary, "model", "ru");

                        // Add the "data_file" field
                        writeFileField(outputStream, boundary, "data_file", audioFile);

                        // End the multipart form
                        outputStream.write(("--" + boundary + "--\r\n").getBytes());
                        outputStream.flush();
                    }

                    int responseCode = connection.getResponseCode();

                    BufferedReader bufferedReader;
                    if (responseCode >= 400) {
                        bufferedReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                    } else {
                        bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    }

                    String line;
                    StringBuilder response = new StringBuilder();

                    while ((line = bufferedReader.readLine()) != null) {
                        response.append(line);
                    }

                    bufferedReader.close();

                    System.out.println("Response Body: " + response.toString());

                    // Извлечь ID задания из ответа
                    String jobId = extractJobId(response.toString());

                    if (jobId != null) {
//                        System.out.println("Job ID: " + jobId);

                        //Отправляем запросы, пока не получим статус "done" или.
                        while (true) {
                            Thread.sleep(10000); // 10 сек

                            URL jobUrlObj = new URL(apiUrl + jobId);
                            HttpURLConnection jobConnection = (HttpURLConnection) jobUrlObj.openConnection();
                            jobConnection.setRequestMethod("GET");
                            jobConnection.setRequestProperty("Authorization", "Bearer " + apiToken);

                            int jobResponseCode = jobConnection.getResponseCode();
//                            System.out.println("Job Response Code: " + jobResponseCode);

                            BufferedReader jobBufferedReader;
                            if (jobResponseCode >= 400) {
                                jobBufferedReader = new BufferedReader(new InputStreamReader(jobConnection.getErrorStream()));
                            } else {
                                jobBufferedReader = new BufferedReader(new InputStreamReader(jobConnection.getInputStream()));
                            }

                            StringBuilder jobResponse = new StringBuilder();
                            while ((line = jobBufferedReader.readLine()) != null) {
                                jobResponse.append(line);
                            }
                            jobBufferedReader.close();

//                            System.out.println("Job Response Body: " + jobResponse.toString());

                            // Извлечь статус из ответа
                            String status = extractJobStatus(jobResponse.toString());
                            System.out.println("Job Status: " + status);

                            if (status != null) {
                                if (status.equals("done")) {
                                    audioFile.delete(); // Удаление файла
                                    break;
                                } else if (status.equals("rejected")) {
                                    // Job is rejected
//                                    System.out.println("Job is rejected.");
                                    break;
                                }
                            }
                        }

                        // Get the transcript
                        String transcriptUrl = apiUrl + jobId + "/transcript?format=txt";
                        URL transcriptUrlObj = new URL(transcriptUrl);
                        HttpURLConnection transcriptConnection = (HttpURLConnection) transcriptUrlObj.openConnection();
                        transcriptConnection.setRequestMethod("GET");
                        transcriptConnection.setRequestProperty("Authorization", "Bearer " + apiToken);

                        int transcriptResponseCode = transcriptConnection.getResponseCode();

                        BufferedReader transcriptBufferedReader;
                        if (transcriptResponseCode >= 400) {
                            transcriptBufferedReader = new BufferedReader(new InputStreamReader(transcriptConnection.getErrorStream()));
                        } else {
                            transcriptBufferedReader = new BufferedReader(new InputStreamReader(transcriptConnection.getInputStream()));
                        }

                        StringBuilder transcriptResponse = new StringBuilder();
                        while ((line = transcriptBufferedReader.readLine()) != null) {
                            transcriptResponse.append(line);
                        }
                        transcriptBufferedReader.close();

                        System.out.println("Audio: " + audioFile.getName() + " transcribed successfully.\n");
                        return transcriptResponse.toString();
                    }
                } catch (IOException | InterruptedException e) {
                    audioFile.delete(); // Удаление файла
                    e.printStackTrace();
                }
            } else {
                audioFile.delete(); // Удаление файла
                System.out.println("Не удалось загрузить аудио из YouTube.");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static void writeFormField(OutputStream outputStream, String boundary, String fieldName, String fieldValue) throws IOException {
        outputStream.write(("--" + boundary + "\r\n").getBytes());
        outputStream.write(("Content-Disposition: form-data; name=\"" + fieldName + "\"\r\n\r\n").getBytes());
        outputStream.write((fieldValue + "\r\n").getBytes());
        outputStream.flush();
    }

    private static void writeFileField(OutputStream outputStream, String boundary, String fieldName, File file) throws IOException {
        outputStream.write(("--" + boundary + "\r\n").getBytes());
        outputStream.write(("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + file.getName() + "\"\r\n").getBytes());
        outputStream.write(("Content-Type: application/octet-stream\r\n\r\n").getBytes());

        byte[] fileBytes = Files.readAllBytes(file.toPath());
        outputStream.write(fileBytes);

        outputStream.write(("\r\n").getBytes());
        outputStream.flush();
    }

    private static String extractJobId(String responseBody) {
        // Extract the "id" value from the JSON response
        String jobId = null;
        try {
            JSONObject json = new JSONObject(responseBody);
            if (json.has("id")) {
                jobId = json.getString("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jobId;
    }

    private static String extractJobStatus(String responseBody) {
        // Extract the "status" value from the JSON response
        String status = null;
        try {
            JSONObject json = new JSONObject(responseBody);
            if (json.has("job") && json.getJSONObject("job").has("status")) {
                status = json.getJSONObject("job").getString("status");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    private static File downloadAudioFromYouTube(String youtubeUrl) throws IOException, InterruptedException {
        String outputFolder = "src/main/resources/TempVideos/";
        String[] command = {"yt-dlp", "-x","--audio-format", "mp3", "-o", outputFolder + "%(id)s", youtubeUrl};
        Process process = Runtime.getRuntime().exec(command);

        // Ждем завершения процесса загрузки аудио
        process.waitFor();

        // Проверяем, была ли загрузка успешной
        if (process.exitValue() == 0) {
            // Получаем список скачанных файлов
            File[] files = new File(outputFolder).listFiles();

            // Ищем скачанный аудиофайл
            for (File file : files) {
                if (file.getName().endsWith(".mp3")) {
                    return file;
                }
            }
        }

        return null;
    }
}