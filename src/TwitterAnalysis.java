import com.alibaba.fastjson.JSON;
import entity.TwitterContent;
import org.apache.commons.lang.time.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class TwitterAnalysis {

    private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd");
    public static Set<String> ENGLISH_LOCATION_NAMES = new HashSet<>(); // uk location
    public static Set<String> AMERICAN_LOCATION_NAMES = new HashSet<>(); // us location
    private static List<String> CHECK_TAG_LIST = new ArrayList<>();

    static {
        ENGLISH_LOCATION_NAMES.add("UK");
        AMERICAN_LOCATION_NAMES.add("USA");

        CHECK_TAG_LIST.add("BLM".toLowerCase());
        CHECK_TAG_LIST.add("georgefloyd".toLowerCase());
        CHECK_TAG_LIST.add("icantbreath".toLowerCase());
    }

    public static void main(String[] args) {
        analysis();
    }

    public static void analysis() {
        System.out.println("Start data analysis ...");
        List<TwitterContent> twitterContentList = parseCleanDate();
        analysisOne(twitterContentList);
        analysisTwo(twitterContentList);
        analysisThree(twitterContentList);
        analysisFour(twitterContentList, 200);
        analysisFive(twitterContentList);
        analysisSix(twitterContentList);
        analysisSeven(twitterContentList);

        System.out.println("Data analysis completed!\n");
    }

    /**
     *  5 most used hashtag used
     * @param twitterContentList
     */
    private static void analysisSeven(List<TwitterContent> twitterContentList) {
        Map<String, Integer> protestTagCountMap = new HashMap<>(); // positive
        Map<String, Integer> notProtestTagCountMap = new HashMap<>(); //Negative
        for (TwitterContent twitterContent : twitterContentList) {
            if (twitterContent.getText().toLowerCase().contains("protest")) {
                // find tweets that contain negative attitude towards protest 
                if (twitterContent.getText().toLowerCase().matches(".*(n’t|n't|no|not).*?protest.*")) {
                    for (String tag : twitterContent.genLowerCaseTagList()) {
                        notProtestTagCountMap.put(tag, notProtestTagCountMap.getOrDefault(tag, 0) + 1);
                    }
                } else {
                    for (String tag : twitterContent.genLowerCaseTagList()) {
                        protestTagCountMap.put(tag, protestTagCountMap.getOrDefault(tag, 0) + 1);
                    }
                }
            }
        }
        System.out.println("\n==============================================================================");
        System.out.println("result_seven: most used 5 keywords in negative opinion and positive opinion ");
        System.out.println("=======");
        protestTagCountMap = sortByValue(protestTagCountMap, false);
        List<String> tagList = new ArrayList<>(protestTagCountMap.keySet());
        for (int i = 0; i < tagList.size() && i < 5; i++) {
            System.out.println("    key word for positive opinion No." + (i + 1) + " : " + tagList.get(i));
        }

        System.out.println("=======");
        notProtestTagCountMap = sortByValue(notProtestTagCountMap, false);
        tagList = new ArrayList<>(notProtestTagCountMap.keySet());
        for (int i = 0; i < tagList.size() && i < 5; i++) {
            System.out.println("    key word for negative opinion No." + (i + 1) + " : " + tagList.get(i));
        }
    }

    /**
     *  the frequency of keyword "trump" in negative opinion
     *  
     * @param twitterContentList
     */
    private static void analysisSix(List<TwitterContent> twitterContentList) {
        Map<String, Integer> tagCountMap = new HashMap<>();
        for (TwitterContent twitterContent : twitterContentList) {
            if (twitterContent.getText().toLowerCase().matches(".*(n’t|n't|no|not).*?protest.*")) {
                for (String tag : twitterContent.genLowerCaseTagList()) {
                    tagCountMap.put(tag, tagCountMap.getOrDefault(tag, 0) + 1);
                }
            }
        }
        int sum = 0;
        int trumpCount = 0;
        for (String tag : tagCountMap.keySet()) {
            sum += tagCountMap.get(tag);
            if ("trump".equals(tag)) {
                trumpCount++;
            }
        }
        if (sum == 0) {
            sum = 1;
        }
        System.out.println("\n==============================================================================");
        System.out.println("restult_six: how often did keyword <trump> appeared in the tweets of negaive opinion   ");
        System.out.println("=======");
        System.out.printf("    frequency of key word trump: %.2f%%\n", trumpCount * 100D / sum);

    }

    /**
     *  people's opinion towards covid-19 in the discussion of protest 
     * @param twitterContentList
     */
    private static void analysisFive(List<TwitterContent> twitterContentList) {
        int notProtestCount = 0;
        int covid19Count = 0;
        for (TwitterContent twitterContent : twitterContentList) {
            if (twitterContent.getText().toLowerCase().matches(".*(n’t|n't|no|not).*?protest.*")) {
                notProtestCount++;
                if (twitterContent.getText().toLowerCase().matches(".*covid[- ]19.*")) {
                    covid19Count++;
                }
            }
        }
        System.out.println("\n==============================================================================");
        System.out.println("restult_five: is the negative opinion related to covid-19? ");
        System.out.println("=======");
        System.out.println("    notProtestCount: " + notProtestCount);
        System.out.println("    covid-19 count: " + covid19Count);

    }

    /**
     * amount of two side of opinions toward protest and the fluctuation within a month of it
     * @param twitterContentList
     */
    private static void analysisFour(List<TwitterContent> twitterContentList, int dateNum) {
        Calendar calendar = generateStartOfDay();
        long endTime = calendar.getTimeInMillis()-1;
        long startTime = endTime - DateUtils.MILLIS_PER_DAY * dateNum;

        Map<String, Integer> protestDailyCountMap = new HashMap<>(); //positive
        Map<String, Integer> notProtestDailyCountMap = new HashMap<>(); // negative
        for (TwitterContent twitterContent : twitterContentList) {
            // out of date
            if (twitterContent.getCreateTime().getTime() < startTime || twitterContent.getCreateTime().getTime() > endTime) {
                continue;
            }
            if (twitterContent.getText().toLowerCase().contains("protest")) {
                String dateStr = DF.format(twitterContent.getCreateTime());
                // regular expression 
                if (twitterContent.getText().toLowerCase().matches(".*(n’t|n't|no|not).*?protest.*")) {
                    int count = notProtestDailyCountMap.getOrDefault(dateStr, 0);
                    notProtestDailyCountMap.put(dateStr, count + 1);
                } else {
                    int count = protestDailyCountMap.getOrDefault(dateStr, 0);
                    protestDailyCountMap.put(dateStr, count + 1);
                }
            }
        }
        System.out.println("\n==============================================================================");
        System.out.println("restult_Four: amount of two side of opinions toward protest and the fluctuation within a month of it");
        System.out.println("=======");
        int sum = 0;
        for (String dateStr : protestDailyCountMap.keySet()) {
            sum += protestDailyCountMap.get(dateStr);
            System.out.println("    positive " + dateStr + " : " + protestDailyCountMap.get(dateStr));
        }
        System.out.println("    total amount of positive opinions" + " : " + sum);

        System.out.println("=======");
        sum = 0;
        for (String dateStr : notProtestDailyCountMap.keySet()) {
            sum += notProtestDailyCountMap.get(dateStr);
            System.out.println("    negative" + dateStr + " : " + notProtestDailyCountMap.get(dateStr));
        }
        System.out.println("    total amount of negative opinions" + " : " + sum);

    }

    /**
     * hashtag that people would also mentioned 
     * @param twitterContentList
     */
    private static void analysisThree(List<TwitterContent> twitterContentList) {
        Map<String, Integer> tagCountMap = new HashMap<>();
        for (TwitterContent twitterContent : twitterContentList) {
            boolean hasTag = false;
            for (String tag : twitterContent.genLowerCaseTagList()) {
                if (CHECK_TAG_LIST.contains(tag)) {
                    hasTag = true;
                    break;
                }
            }

            if (hasTag) {
                List<String> tagList = new ArrayList<>(twitterContent.genLowerCaseTagList());
                tagList.removeAll(CHECK_TAG_LIST);
                for (String tag : tagList) {
                    int count = tagCountMap.getOrDefault(tag, 0);
                    tagCountMap.put(tag, count + 1);
                }
            }
        }
        System.out.println("\n==============================================================================");
        System.out.println("result_Three:hashtag that people would also mentioned ");
        System.out.println("=======");
        int sum = 0;
        int protest = 0;
        for (String tag : tagCountMap.keySet()) {
            sum += tagCountMap.get(tag);
            if (tag.equals("protest")) {
                protest += tagCountMap.get(tag);
            }
            System.out.println("    " + tag + " : " + tagCountMap.get(tag));
        }
        System.out.println("=======");
        if (sum == 0) {
            sum = 1;
        }
        System.out.printf("    frequency of #protest: %.2f%%\n", protest * 100D / sum);

    }

    /**
     * the amount of hashtag after 6/01/2020
     * @param twitterContentList
     */
    private static void analysisTwo(List<TwitterContent> twitterContentList) {
        Calendar start = generateTime(2020, 6, 01);
        long startTime = start.getTimeInMillis();
        long endTime = startTime + DateUtils.MILLIS_PER_DAY * 20; // 20days after 06/01/2020

        Map<String, Integer> countMap = new TreeMap<>();
        for (TwitterContent twitterContent : twitterContentList) {
            if (twitterContent.getCreateTime().getTime() < startTime || twitterContent.getCreateTime().getTime() > endTime) {
                continue;
            }
            boolean hasTag = false;
            for (String tag : twitterContent.genLowerCaseTagList()) {
                if (CHECK_TAG_LIST.contains(tag)) {
                    hasTag = true;
                    break;
                }
            }
            if (hasTag) {
                String dateStr = DF.format(twitterContent.getCreateTime());
                int count = countMap.getOrDefault(dateStr, 0);
                countMap.put(dateStr, count + 1);
            }
        }

        System.out.println("\n==============================================================================");
        System.out.println("restult_Two: the amount of hashtag after 6/01/2020");
        System.out.println("=======");
        for (String dateStr : countMap.keySet()) {
            System.out.println("    " + dateStr + " : " + countMap.get(dateStr));
        }
    }

    /**
     * the usage of hashtags in uk and usa
     * @param twitterContentList
     */
    private static void analysisOne(List<TwitterContent> twitterContentList) {
        Map<String, Integer> EnglishCountMap = new TreeMap<>();
        Map<String, Integer> AmericaCountMap = new TreeMap<>();
        for (TwitterContent twitterContent : twitterContentList) {
            String location = twitterContent.getLocation();
            String dateStr = DF.format(twitterContent.getCreateTime());
            if (isEnglish(location)) {
                int count = EnglishCountMap.getOrDefault(dateStr, 0);
                EnglishCountMap.put(dateStr, count + 1);
            } else if (isAmerican(location)) {
                int count = AmericaCountMap.getOrDefault(dateStr, 0);
                AmericaCountMap.put(dateStr, count + 1);
            }
        }

        System.out.println("\n==============================================================================");
        System.out.println("restult_One: the usage of hashtags in uk and usa");
        System.out.println("=======");
        for (String dateStr : EnglishCountMap.keySet()) {
            System.out.println("    UK " + dateStr + " : " + EnglishCountMap.get(dateStr));
        }
        System.out.println("=======");
        for (String dateStr : AmericaCountMap.keySet()) {
            System.out.println("    USA " + dateStr + " : " + AmericaCountMap.get(dateStr));
        }
    }

    /**
     * UK user
     * @param location 
     * @return
     */
    private static boolean isEnglish(String location ) {
        if (location == null) {
            return false;
        }
        for (String locationName : ENGLISH_LOCATION_NAMES) {
            if (location.contains(locationName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * USA user
     * @param location 
     * @return
     */
    private static boolean isAmerican(String location ) {
        if (location == null) {
            return false;
        }
        for (String locationName : AMERICAN_LOCATION_NAMES) {
            if (location.contains(locationName)) {
                return true;
            }
        }
        return false;
    }

    private static List<TwitterContent> parseCleanDate() {
        List<TwitterContent> list = new ArrayList<>();
        List<String> lines = FileUtil.readFileString(ApplicationMain.OUTPUT_TARGET_FILE);
        for (String line : lines) {
            TwitterContent twitterContent = JSON.parseObject(line.trim(), TwitterContent.class);
            list.add(twitterContent);
        }
        return list;
    }

    /**
     * generate date
     * @param year
     * @param month
     * @param date
     * @return
     */
    private static Calendar generateTime(int year, int month, int date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month + 1);
        calendar.set(Calendar.DATE, date);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }

    /**
     * start of the day
     * @return
     */
    private static Calendar generateStartOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }

    /** sort based on value 
     * @param map
     * @param asc true: ascending order
     * @return
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, final boolean asc) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                if (asc) {
                    return (o1.getValue()).compareTo(o2.getValue());
                } else {
                    return (o2.getValue()).compareTo(o1.getValue());
                }
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }


}
