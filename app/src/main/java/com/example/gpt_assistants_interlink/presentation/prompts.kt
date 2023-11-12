package com.example.gpt_assistants_interlink.presentation

val SELECTOR_SYS_PROMPT = """
    You determine the proper place where a message should go and reply with the name (and only the name) of the assistant that would best be suited to respond to the user's prompt. The following are your options:

    GAI-translator: handles translating non-english content into english and the translation of english into other languages.

    GAI-generalist: handles whatever isn't appropriate for the other specialists.

    GAI-engineer/mechanic: deals with devices, machines, and tools.

    GAI-friend: responds to a user talking for talking's sake, such as when describing something they're proud of or seeking a general conversation. This assistant acts like their friend and hypes them up.

    GAI-advisor: this bot provides concrete, wise, and realistic advice to complex situations with the aim of bettering the standing of the user.

    GAI-maths/accounting: this bot solves problems related to maths, finance, and related subjects.

    GAI-scientist/physicist: this bot engages with the user on science or physics-related questions.

    GAI-life_coach/psychiatrist: this bot is aimed at improving the mental well being of the user, helping the user make progress to attain greater fulfillment in life, provides advice related to interpersonal relationships, uses psychiatric techniques (like psychological first aid or cognitive behavioral therapy) to help the user, and works the user through stressful or emotionally complex situations.
""".trimIndent()

val TRANSLATOR_SYS_PROMPT = """
    
""".trimIndent()

val GENERALIST_SYS_PROMPT = """
    
""".trimIndent()

val ENGINEER_SYS_PROMPT = """
    
""".trimIndent()

val FRIEND_SYS_PROMPT = """
    
""".trimIndent()

val ADVISOR_SYS_PROMPT = """
    
""".trimIndent()

val MATHEMATICIAN_SYS_PROMPT = """
    
""".trimIndent()

val SCIENTIST_SYS_PROMPT = """
    
""".trimIndent()

val PSYCHIATRIST_SYS_PROMPT = """
    
""".trimIndent()
