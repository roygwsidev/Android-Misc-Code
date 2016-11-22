
import android.content.Context;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

import static java.lang.System.currentTimeMillis;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.URLConnection.guessContentTypeFromName;
import static java.text.MessageFormat.format;
import static java.util.logging.Level.INFO;
import static java.util.logging.Logger.getLogger;

class MultipartUtility {
    private static final String TAG = MultipartUtility.class.getSimpleName();
    private static final Logger log = getLogger(MultipartUtility.class
            .getName());
    private static final String CRLF = "\r\n";
    private static final String CHARSET = "UTF-8";

    private final HttpURLConnection connection;
    private final OutputStream outputStream;
    private final PrintWriter writer;
    private final String boundary;
    private final static int maxBufferSize = 1024;
    private MediaProgressListener listener;


    // for log formatting only
    private final URL url;
    private final long start;

    MultipartUtility(Context context, final String urlString, MediaProgressListener listener, ServerApiRequest apiRequest) throws IOException {
        this.listener = listener;
        URL url = new URL(urlString);
        start = currentTimeMillis();
        this.url = url;

        // boundary = "---------------------------" + currentTimeMillis();
        boundary = "---" + currentTimeMillis();

        connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(1000*5);
        connection.setReadTimeout(1000*5);
        connection.setChunkedStreamingMode(maxBufferSize);
        connection.setRequestMethod("POST");
        connection.setChunkedStreamingMode(0);
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("Keep-Alive", 1000*5);
        connection.setRequestProperty("Accept-Charset", CHARSET);
        connection.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + boundary);
        connection.setRequestProperty("Authorization", apiRequest.getBasicAuthString(SwipeApplication.getSessionManager(context)));
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);

        outputStream = connection.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, CHARSET),
                true);
    }

    void addFormField(final String name, final String value) {
        writer.append("--").append(boundary).append(CRLF)
                .append("Content-Disposition: form-data; name=\"").append(name)
                .append("\"").append(CRLF)
                .append("Content-Type: text/plain; charset=").append(CHARSET)
                .append(CRLF).append(CRLF).append(value).append(CRLF);
    }

    void addFilePart(String fieldName, String path) throws IOException {
        addFilePart(fieldName, new File(path));
    }

    void addFilePart(final String fieldName, final File uploadFile)
            throws IOException {
        final String fileName = uploadFile.getName();
        writer.append("--").append(boundary).append(CRLF)
                .append("Content-Disposition: form-data; name=\"")
                .append(fieldName).append("\"; filename=\"").append(fileName)
                .append("\"").append(CRLF).append("Content-Type: ")
                .append(guessContentTypeFromName(fileName)).append(CRLF)
                .append("Content-Transfer-Encoding: binary").append(CRLF)
                .append(CRLF);
        writer.flush();
        outputStream.flush();
        long bytesTransferred = 0;
        try {
            final FileInputStream inputStream = new FileInputStream(uploadFile);
            long totalSize = uploadFile.length();
            int bytesAvailable = inputStream.available();
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            final byte[] buffer = new byte[bufferSize];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer, 0, bufferSize)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                bytesTransferred += bytesRead;
                if (listener != null) {
                    listener.transferred((int) (100 * (bytesTransferred / (float) totalSize)));
                }
            }
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        writer.append(CRLF);
    }

    public void addHeaderField(String name, String value) {
        writer.append(name).append(": ").append(value).append(CRLF);
    }

    public JSONObject finish() throws Exception {
        writer.append(CRLF).append("--").append(boundary).append("--")
                .append(CRLF);
        writer.close();

        final int status = connection.getResponseCode();
        if (status != HTTP_OK) {
            throw new IOException(format("{0} failed with HTTP status: {1}",
                    url, status));
        }
        try {
            final InputStream is = connection.getInputStream();
            final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            final byte[] buffer = new byte[maxBufferSize];
            int bytesRead;
            while ((bytesRead = is.read(buffer, 0, maxBufferSize)) != -1) {
                bytes.write(buffer, 0, bytesRead);
            }
            log.log(INFO,
                    format("{0} took {4} ms", url,
                            (currentTimeMillis() - start)));
            String response = new String(bytes.toByteArray());
            return new JSONObject(response);
            /*BufferedReader br = null;
            Log.e("HTTP success code", "" + connection.getResponseCode());
            InputStream inputStream = connection.getInputStream();
            if (inputStream != null) {
                br = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder builder = new StringBuilder();
                String output;
                while ((output = br.readLine()) != null) {
                    builder.append(output);
                }
                return new JSONObject(builder.toString());
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return null;
    }
}