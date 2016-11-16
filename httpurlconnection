
/***
	Http Url Connection get request
	***/

JSONObject HttpGetRequest(String urlString) throws IOException {
        InputStream is = null;
        try {
            java.net.URL url = new java.net.URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Authorization","");
            urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
            urlConnection.setDoInput(true);
            urlConnection.setConnectTimeout(MAX_CONNECTION_TIME_OUT);
            urlConnection.setReadTimeout(MAX_SOCKET_TIME_OUT);
            urlConnection.setRequestMethod("GET");
            is = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream(), "iso-8859-1"), 1024);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString();
            return new JSONObject(json);
        } catch (Exception e) {
            Log.d(TAG, "Failed to parse", e);
            throw new IOException("Fail to get data " + e.getMessage());
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }
	
	
	/***
	Http Url Connection post request
	***/
	JSONObject HttpPostRequest(String urlString, String params) {
        InputStream is = null;
        try {
            Log.d("post request", urlString);
            java.net.URL url = new java.net.URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty(HttpHeaders.AUTHORIZATION, "");
            urlConnection.setRequestProperty(HttpHeaders.ACCEPT_CHARSET, "UTF-8");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setConnectTimeout(MAX_CONNECTION_TIME_OUT);
            urlConnection.setReadTimeout(MAX_SOCKET_TIME_OUT);
            urlConnection.setRequestMethod("POST");

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    urlConnection.getOutputStream());
            wr.writeBytes(params);
            wr.flush();
            wr.close();

            // Get Response
            is = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream(), "iso-8859-1"), 1024);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString();
            return new JSONObject(json);
        } catch (Exception e) {
            Log.d(TAG, "Failed to parse", e);
            return null;
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }
