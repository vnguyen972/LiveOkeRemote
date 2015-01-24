package com.vnguyen.liveokeremote.helper;

import android.util.Log;

import com.vnguyen.liveokeremote.LiveOkeRemoteApplication;
import com.vnguyen.liveokeremote.data.KeySong;
import com.vnguyen.liveokeremote.data.Song;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SongHelper {
    public static String JSEARCH_API_CODE = "d5d0cb6a-e7c7-422e-a9f4-9103f4d06276";

    public static String getLyric(String url) {
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = null;
        try {
            httpGet = new HttpGet(url);
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            //Log.v(LiveOkeRemoteApplication.TAG,"status code = " + statusCode);
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    //Log.v(LiveOkeRemoteApplication.TAG,"line = " + line);
                    builder.append(line);
                }
                //Log.v(LiveOkeRemoteApplication.TAG,"builder.toString = " + builder.toString());
            } else {
                Log.e(LiveOkeRemoteApplication.TAG, "Failed to search song");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public static String unescapeJava(String escaped) {
        if(escaped.indexOf("\\u")==-1)
            return escaped;

        String processed="";

        int position=escaped.indexOf("\\u");
        while(position!=-1) {
            if(position!=0)
                processed+=escaped.substring(0,position);
            String token=escaped.substring(position+2,position+6);
            escaped=escaped.substring(position+6);
            processed+=(char)Integer.parseInt(token,16);
            position=escaped.indexOf("\\u");
        }
        processed+=escaped;

        return processed;
    }

    public static String searchSong(String title) {
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = null;
        try {
            httpGet = new HttpGet("http://j.ginggong.com/jOut.ashx?code="+ JSEARCH_API_CODE +
                    "&k="+ URLEncoder.encode(title, "utf-8"));
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                Log.e(LiveOkeRemoteApplication.TAG, "Failed to search song");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return unescapeJava(builder.toString());
    }

    private static String makeItQuick(String title) throws Exception {
        String quick = "";
        StringTokenizer stok = new StringTokenizer(title," ");
        if (stok.countTokens() == 1) {
            // try "_"
            stok = new StringTokenizer(title,"_");
        }
        while (stok.hasMoreTokens()) {
            String token = stok.nextToken();
            quick += token.substring(0, 1);
        }
        return quick;
    }
    /**
     * Build a song base on a line of data Here's the structure of data (; is
     * the delimeter) 50001;Bài Ca Tết Cho Em(title);Quang Lê(singer);;Quốc
     * Dũng(author); TNK 70_27(producer) ; G:\MyStuff\HDKaraoke\pbn70\TNK 70_27
     * - Quang Lê - Bài Ca Tết Cho Em.VOB(path);
     * .VOB(type);NhacVang(classified);D (Tone);0(bpm) ;VN(language);
     * 0(favorites);False(swap);2(popular rank);; Tết này anh không thèm kẹo
     * mứt. Vì đã có...(lyrics); 0.9815084(volume);Requester
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static Song buildSong(String data) throws Exception {
        Song song = new Song();
        // Log.v("K7 info",data);
        byte[] b = null;
        try {
            b = data.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String s = new String(b);
        String[] str = s.split(";");
        song.id= str[0];
        if (str[1].equals("")) {
            song.title = "?Unavailable";
        } else {
            song.title = str[1];
        }
        //song.setConvertedTitle(decompose(str[1]));
        song.convertedTitle = convertAllChars(str[1]);
        if (str[2].equals("")) {
            song.singer = "?Unavailable";
        } else {
            song.singer = str[2];
        }
        song.quickTitle = makeItQuick(song.convertedTitle);
        //song.setConvertedSinger(decompose(str[2]));
        song.convertedSinger = convertAllChars(str[2]);
        song.quickSinger = makeItQuick(song.convertedSinger);
        song.singerIcon = str[3];
        song.author = str[4];
        song.convertedAuthor = convertAllChars(str[4]);
        song.producer = str[5];
        song.convertProducer = convertAllChars(str[5]);
        song.songPath = str[6];
        song.type = str[7];
        song.classified = str[8];
        song.tone = str[9];
        song.tempo = str[10];
        song.language = str[11].trim();
        song.favorites = str[12];
        song.swapped = str[13];
        song.popularRank = str[14];
        // skip one
        song.lyrics = str[16];
        song.volume = str[17];
        song.requester = str[18];
        return song;
    }

    /**
     * Convert UTF-8 string to normal string for easy to compare when searching
     *
     * @param string
     * @return
     * @throws Exception
     */
    public static String convertAllChars(String string) throws Exception {
        char[] chars = new char[string.length()];
        char[] chars1 = string.toCharArray();
        for (int i = 0, size = chars1.length; i < size; i++) {
            String s = String.valueOf(chars1[i]);
            Characters c = Characters.forString(s.toUpperCase(Locale.US));
            if (c != null) {
                switch (c) {
                    case ONE:case TWO:case THREE:case FOUR:case FIVE:case SIX:
                    case SEVEN:case EIGHT:case NINE:case ZERO:
                        chars[i] = s.charAt(0);
                        break;
                    case A1:case A2:case A3:case A4:case A5:case A6:case A7:case A8:
                    case A9:case A10:case A11:case A12:case A13:case A14:case A15:
                    case A16:case A17:case A18:case A19:case A20:case A21:case A22:
                    case A23:case A24:case A25:case A26:case A27:case A28:case A29:
                    case A30:
                        chars[i] = "A".charAt(0);
                        break;
                    case B:case C:case F:case G:case H:case J:case K:case L:
                    case M:case N:case P:case Q:case R:case S:case T:case V:
                    case W:case X:case Z:
                        chars[i] = s.toUpperCase(Locale.US).charAt(0);
                        break;
                    case D1:case D2:case D3:
                        chars[i] = "D".charAt(0);
                        break;
                    case E1:case E2:case E3:case E4:case E5:case E6:case E7:case E8:
                    case E9:case E10:case E11:case E12:case E13:case E14:case E15:
                    case E16:case E17:case E18:case E19:case E20:
                        chars[i] = "E".charAt(0);
                        break;
                    case I1:case I2:case I3:case I4:case I5:case I6:case I7:
                        chars[i] = "I".charAt(0);
                        break;
                    case O1:case O2:case O3:case O4:case O5:case O6:case O7:
                    case O8:case O9:case O10:case O11:case O12:case O13:
                    case O14:case O15:case O16:case O17:case O18:case O19:
                    case O20:case O21:case O22:case O23:case O24:case O25:case O26:
                        chars[i] = "O".charAt(0);
                        break;
                    case U1:case U2:case U3:case U4:case U5:case U6:case U7:
                    case U8:case U9:case U10:case U11:case U12:case U13:
                    case U14:case U15:case U16:case U17:case U18:case U19:
                        chars[i] = "U".charAt(0);
                        break;
                    case Y1:case Y2:case Y3:case Y4:case Y5:case Y6:case Y7:

                        chars[i] = "Y".charAt(0);
                        break;
                }
            } else {
                chars[i] = s.charAt(0);
            }
        }
        return new String(chars);
    }

    public static String translateKey(String key) throws Exception {
        String s = "(";
        if (key.equalsIgnoreCase("#")) {
            s += "'1','2','3','4','5','6','7','8','9','0'";
        } else if (key.equalsIgnoreCase("A")) {
            s += "'" + Characters.A1.getStr() + "','" + Characters.A2.getStr() + "',";
            s += "'" + Characters.A3.getStr() + "','" + Characters.A4.getStr() + "',";
            s += "'" + Characters.A5.getStr() + "','" + Characters.A6.getStr() + "',";
            s += "'" + Characters.A7.getStr() + "','" + Characters.A8.getStr() + "',";
            s += "'" + Characters.A9.getStr() + "','" + Characters.A10.getStr() + "',";
            s += "'" + Characters.A11.getStr() + "','" + Characters.A12.getStr() + "',";
            s += "'" + Characters.A13.getStr() + "','" + Characters.A14.getStr() + "',";
            s += "'" + Characters.A15.getStr() + "','" + Characters.A16.getStr() + "',";
            s += "'" + Characters.A17.getStr() + "','" + Characters.A18.getStr() + "',";
            s += "'" + Characters.A19.getStr() + "','" + Characters.A20.getStr() + "',";
            s += "'" + Characters.A21.getStr() + "','" + Characters.A22.getStr() + "',";
            s += "'" + Characters.A23.getStr() + "','" + Characters.A24.getStr() + "'";
            s += "'" + Characters.A25.getStr() + "','" + Characters.A26.getStr() + "'";
            s += "'" + Characters.A27.getStr() + "','" + Characters.A28.getStr() + "'";
            s += "'" + Characters.A29.getStr() + "','" + Characters.A30.getStr() + "'";
        } else if (key.equalsIgnoreCase("D")) {
            s += "'" + Characters.D1.getStr() + "','" + Characters.D2.getStr() + "',";
            s += "'" + Characters.D3.getStr() + "'";
        } else if (key.equalsIgnoreCase("E")) {
            s += "'" + Characters.E1.getStr() + "','" + Characters.E2.getStr() + "',";
            s += "'" + Characters.E3.getStr() + "','" + Characters.E4.getStr() + "',";
            s += "'" + Characters.E5.getStr() + "','" + Characters.E6.getStr() + "',";
            s += "'" + Characters.E7.getStr() + "','" + Characters.E8.getStr() + "',";
            s += "'" + Characters.E9.getStr() + "','" + Characters.E10.getStr() + "',";
            s += "'" + Characters.E11.getStr() + "','" + Characters.E12.getStr() + "',";
            s += "'" + Characters.E13.getStr() + "','" + Characters.E14.getStr() + "',";
            s += "'" + Characters.E15.getStr() + "','" + Characters.E16.getStr() + "',";
            s += "'" + Characters.E17.getStr() + "','" + Characters.E18.getStr() + "',";
            s += "'" + Characters.E19.getStr() + "','" + Characters.E20.getStr() + "',";

        } else if (key.equalsIgnoreCase("I")) {
            s += "'" + Characters.I1.getStr() + "','" + Characters.I2.getStr() + "',";
            s += "'" + Characters.I3.getStr() + "','" + Characters.I4.getStr() + "',";
            s += "'" + Characters.I5.getStr() + "','" + Characters.I6.getStr() + "',";
            s += "'" + Characters.I7.getStr() + "'";
        } else if (key.equalsIgnoreCase("O")) {
            s += "'" + Characters.O1.getStr() + "','" + Characters.O2.getStr() + "',";
            s += "'" + Characters.O3.getStr() + "','" + Characters.O4.getStr() + "',";
            s += "'" + Characters.O5.getStr() + "','" + Characters.O6.getStr() + "',";
            s += "'" + Characters.O7.getStr() + "','" + Characters.O8.getStr() + "',";
            s += "'" + Characters.O9.getStr() + "','" + Characters.O10.getStr() + "',";
            s += "'" + Characters.O11.getStr() + "','" + Characters.O12.getStr() + "',";
            s += "'" + Characters.O13.getStr() + "','" + Characters.O14.getStr() + "',";
            s += "'" + Characters.O15.getStr() + "','" + Characters.O16.getStr() + "',";
            s += "'" + Characters.O17.getStr() + "','" + Characters.O18.getStr() + "',";
            s += "'" + Characters.O19.getStr() + "','" + Characters.O20.getStr() + "',";
            s += "'" + Characters.O21.getStr() + "','" + Characters.O22.getStr() + "'";
            s += "'" + Characters.O23.getStr() + "','" + Characters.O24.getStr() + "'";
            s += "'" + Characters.O25.getStr() + "','" + Characters.O26.getStr() + "'";
        } else if (key.equalsIgnoreCase("U")) {
            s += "'" + Characters.U1.getStr() + "','" + Characters.U2.getStr() + "',";
            s += "'" + Characters.U3.getStr() + "','" + Characters.U4.getStr() + "',";
            s += "'" + Characters.U5.getStr() + "','" + Characters.U6.getStr() + "',";
            s += "'" + Characters.U7.getStr() + "','" + Characters.U8.getStr() + "',";
            s += "'" + Characters.U9.getStr() + "','" + Characters.U10.getStr() + "',";
            s += "'" + Characters.U11.getStr() + "','" + Characters.U12.getStr() + "',";
            s += "'" + Characters.U13.getStr() + "','" + Characters.U14.getStr() + "',";
            s += "'" + Characters.U15.getStr() + "','" + Characters.U16.getStr() + "'";
            s += "'" + Characters.U17.getStr() + "','" + Characters.U18.getStr() + "'";
            s += "'" + Characters.U19.getStr() + "'";
        } else if (key.equalsIgnoreCase("Y")) {
            s += "'" + Characters.Y1.getStr() + "','" + Characters.Y2.getStr() + "'";
            s += "'" + Characters.Y3.getStr() + "','" + Characters.Y4.getStr() + "'";
            s += "'" + Characters.Y5.getStr() + "','" + Characters.Y6.getStr() + "'";
            s += "'" + Characters.Y7.getStr() + "'";
        } else {
            s += "'" + key + "'";
        }
        s += ")";
        Log.d(LiveOkeRemoteApplication.TAG, s);
        return s;
    }

    public static String getTheKey(String letter) throws Exception {
        String key = "Unknown";
        //Log.d(LiveOkeRemoteApplication.TAG, "the letter = " + letter);
        Characters c = Characters.forString(letter.toUpperCase(Locale.US));
        if (c != null) {
            switch (c) {
                case ONE:case TWO:case THREE:case FOUR:case FIVE:case SIX:
                case SEVEN:case EIGHT:case NINE:case ZERO:
                    key = "#";
                    break;
                default:
                    key = convertAllChars(letter.toUpperCase(Locale.US));
                    break;
            }
        } else {
            key = letter;
        }
        return key;
    }

    /**
     * List the songs on the songlist by the singer
     *
     * @param songList
     * @return
     * @throws Exception
     */
    public ConcurrentHashMap<String, ArrayList<Song>> listSongsBySinger(
            ArrayList<Song> songList) throws Exception {
        final ConcurrentHashMap<String, ArrayList<Song>> map = new ConcurrentHashMap<String, ArrayList<Song>>();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CompletionService<KeySong> pool = new ExecutorCompletionService<KeySong>(executor);
        for (final Song song : songList) {
//			final Song song = songList.get(i);
            pool.submit(new Callable<KeySong>() {
                @Override
                public KeySong call() {
                    try {
                        KeySong ks = new KeySong();
                        ks.song = song;
                        String singer = song.singer;
                        if (singer.equals("")) {
                            singer = "?Unavailable";
                        }
                        String s = song.singer.substring(0, 1);
                        char c = s.charAt(0);
                        String key = "";
                        if (!Character.isLetterOrDigit(c)) {
                            key = "?";
                        } else {
                            key = getTheKey(s);
                        }
                        ks.key = key;
                        return ks;
                    } catch (Exception e) {
                        Log.e(LiveOkeRemoteApplication.TAG, e.getLocalizedMessage(),e);
                    }
                    return null;
                }
            });
        }
        int slSize = songList.size();
        for (int x = 0; x < slSize;x++) {
            KeySong ks = pool.take().get();
            ArrayList<Song> list = new ArrayList<Song>();
            ArrayList<Song> exist = map.putIfAbsent(ks.key, list);
            if (exist != null) {
                list = exist;
            }
            list.add(ks.song);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {}
        return map;
    }

    /**
     * List the songs on the songlist by the title
     *
     * @param songList
     * @return
     * @throws Exception
     */
    public ConcurrentHashMap<String, ArrayList<Song>> listSongsByTittle(
            final ArrayList<Song> songList) throws Exception {
        final ConcurrentHashMap<String, ArrayList<Song>> map = new ConcurrentHashMap<String, ArrayList<Song>>();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CompletionService<KeySong> pool = new ExecutorCompletionService<KeySong>(executor);
        for (final Song song : songList) {
            pool.submit(new Callable<KeySong>() {
                @Override
                public KeySong call() {
                    try {
                        KeySong ks = new KeySong();
                        ks.song = song;
                        String title = song.title.trim();
                        if (title.equals("")) {
                            title = "?Unavailable";
                        }
                        String s = song.title.substring(0, 1);
                        char c = s.charAt(0);
                        String key = "";
                        if (!Character.isLetterOrDigit(c)) {
                            key = "?";
                        } else {
                            key = getTheKey(s);
                        }
                        ks.key = key;
                        return ks;
                    } catch (Exception e) {
                        Log.d(LiveOkeRemoteApplication.TAG,
                                "Song: '" + song.title + "' (" +
                                        song.id + ")");
                        Log.e(LiveOkeRemoteApplication.TAG, e.getLocalizedMessage(),e);
                    }
                    return null;
                }
            });
        }
        int slSize = songList.size();
        for (int x = 0; x < slSize;x++) {
            KeySong ks = pool.take().get();
            ArrayList<Song> list = new ArrayList<Song>();
            ArrayList<Song> exist = map.putIfAbsent(ks.key, list);
            if (exist != null) {
                list = exist;
            }
            list.add(ks.song);
        }
        executor.shutdown();
        while (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {}
        return map;
    }



    /**
     * @param args
     */
    public static void main(String[] args) {
//		SongUtil su = new SongUtil();
//		try {
//			String searchStr = "";
//			su.getSongList().addAll(su.readFile("songs.list"));
//			// su.getSongMap().putAll(su.search(searchStr, su.getSongList()));
//			su.getSongMap().putAll(su.listSongsByTittle(su.getSongList()));
//			System.out.println(su.getSongMap().size());
//			Set<String> keys = su.getSongMap().keySet();
//			ArrayList<String> sortedKeys = new ArrayList<String>(su
//					.getSongMap().size());
//			sortedKeys.addAll(keys);
//			Collections.sort(sortedKeys);
//			int total = 0;
//			for (Iterator<String> it = sortedKeys.iterator(); it.hasNext();) {
//				String key = it.next();
//				ArrayList<Song> songs = su.getSongMap().get(key);
//				total += songs.size();
//				System.out.println("**** " + key + ": " + songs.size()
//						+ " songs. *****");
//				if (searchStr == null || !searchStr.equals("")) {
//					for (int i = 0; i < songs.size(); i++) {
//						Song s = songs.get(i);
//						System.out.println("[" + s.getId() + "]" + s.getTitle()
//								+ "-" + s.getSinger() + "-" + s.getSongPath());
//					}
//				}
//			}
//			System.out.println("Total songs: " + total);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
    }

    public enum Characters {
        ONE("1"), TWO("2"), THREE("3"), FOUR("4"), FIVE("5"), SIX("6"), SEVEN("7"), EIGHT("8"),
        NINE("9"), ZERO("0"), A1("A"), A2("Á"), A3("À"), A4("Ả"), A5("Ã"), A6("Ạ"), A7("Ă"),
        A8("Ắ"), A9("Ằ"), A10("Ẳ"), A11("Ẵ"), A12("Ặ"), A13("Â"), A14("Ấ"), A15("Ầ"), A16("Ẩ"),
        A17("Ẫ"), A18("Ậ"), A19("Á"), A20("À"), A21("Ã"), A22("Ầ"), A23("Á"), A24("Ắ"),

        A25("Á"),A26("Ấ"),A27("Á"),A28("Ả"),A29("À"),A30("Ạ"),

        B("B"), C("C"), F("F"), G("G"), H("H"), J("J"), K("K"), L("L"), M("M"), N("N"), P("P"),
        Q("Q"), R("R"), S("S"), T("T"), V("V"), W("W"), X("X"), Z("Z"), D1("D"), D2("Ð"), D3("Đ"),
        E1("E"), E2("É"), E3("È"), E4("Ẻ"), E5("Ẽ"), E6("Ẹ"), E7("Ê"), E8("Ế"), E9("Ề"), E10("Ể"),
        E11("Ễ"), E12("Ệ"), E13("Ế"), E14("Ễ"), E15("Ế"), E16("Ế"), E17("Ề"),E18("Ẽ"),E19("Ế"),

        E20("Ệ"),

        I1("I"),I2("Í"), I3("Ì"), I4("Ỉ"), I5("Ĩ"), I6("Ị"), I7("Í"), O1("O"), O2("Ô"), O3("Ở"), O4("Ơ"),
        O5("Ọ"), O6("Ó"), O7("Ò"), O8("Ỏ"), O9("Õ"), O10("Ố"), O11("Ồ"), O12("Ổ"), O13("Ỗ"),
        O14("Ộ"), O15("Ớ"), O16("Ờ"), O17("Ỡ"), O18("Ợ"), O19("Ồ"), O20("Ở"), O21("Ỡ"),O22("Ớ"),

        O23("Ọ"),O24("Ố"),O25("Ọ"),O26("Ộ"),

        U1("U"), U2("Ú"), U3("Ù"), U4("Ủ"), U5("Ũ"), U6("Ụ"), U7("Ư"), U8("Ứ"), U9("Ừ"), U10("Ử"),
        U11("Ữ"), U12("Ự"), U13("Ủ"), U14("Ù"), U15("Ü"), U16("Ù"),U17("Ư"),

        U18("Ù"),U19("Ũ"),

        Y1("Y"), Y2("Ý"), Y3("Ỳ"),Y4("Ỷ"), Y5("Ỹ"), Y6("Ỵ"),

        Y7("Ý");

        private final String str;
        private static ConcurrentHashMap<String, Characters> map;

        Characters(String string) {
            str = string;
            map(str, this);
        }

        private void map(String str, Characters characters) {
            if (map == null) {
                map = new ConcurrentHashMap<String, Characters>();
            }
            map.putIfAbsent(str, characters);
        }

        public static Characters forString(String str) {
            return map.get(str);
        }

        public String getStr() {
            return str;
        }
    }

    /**
     * @deprecated
     * @param s
     * @return
     */
    public static String decompose(String s) {
        return java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+","");
    }

}
