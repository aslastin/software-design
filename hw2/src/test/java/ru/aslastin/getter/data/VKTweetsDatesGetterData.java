package ru.aslastin.getter.data;

public class VKTweetsDatesGetterData {
    public static final String JSON_TEST1_RESPONSE = """      
            {
                "response": {
                    "count": 1000,
                    "items": [
                        {
                            "id": 1,
                            "date": 123,
                            "text": "#test1 and text"
                        },
                        {
                            "id": 2,
                            "date": 124,
                            "text": "#test1 and text again"
                        }
                    ],
                    "total_count": 26432428
                }
            }
            """;

    public static final String JSON_TEST2_RESPONSE1 = """      
            {
                "response": {
                    "count": 1000,
                    "items": [
                        {
                            "id": 4,
                            "date": 200,
                            "text": "#test2 ..."
                        }
                    ],
                    "next_from": "3/225886670_4251",
                    "total_count": 725073
                }
            }
            """;

    public static final String JSON_TEST2_RESPONSE2 = """
            {
                "response": {
                    "count": 1000,
                    "items": [
                        {
                            "id": 5,
                            "date": 201,
                            "text": "#test2 ..."
                        },
                        {
                            "id": 4419,
                            "date": 202,
                            "text": "#test2 ."
                        },
                        {
                            "id": 6,
                            "date": 203,
                            "text": "#test2 .."
                        },
                    ],
                    "total_count": 725073
                }
            }
            """;
}
