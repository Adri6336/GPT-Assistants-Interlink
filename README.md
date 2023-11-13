# GPT-Assistants-Interlink
This is a mobile assistant tool for your Android smart-watch designed to augment your abilities on the go and provide virtual companionship through the use of specialized instances. 

Designed to work best on Ticwatch Pro 3 Ultra.

# Concept

![screen](https://github.com/Adri6336/GPT-Assistants-Interlink/assets/64619524/78d1c782-f47e-41c4-8b3f-3e77bfb0af59)

You send a message to the interlink and it routes you to the assistant specialist that would best respond to your message. These specialist assistants are created by the app on first startup, and recalled if memory is lost. By default, all assistants that are expected to make use of code interpreter well will have it enabled for them.

The following specialists are availible to communicate with through GPT-Assistants-Interlink:

- GAI-translator: handles translating non-english content into english and the automatic translation of english into the last used non-english language.
    
- GAI-generalist: handles whatever isn't appropriate for the other specialists.

- GAI-engineer/mechanic: deals with devices, machines, and tools.

- GAI-friend: responds to a user talking for talking's sake, such as when describing something they're proud up or seeking a general conversation. This assistant acts like their virtual friend.

- GAI-advisor: this bot provides concrete, wise, and realistic advice to complex situations with the aim of bettering the standing of the user.

- GAI-maths/accounting: this bot solves problems related to maths, finance, and related subjects.

- GAI-scientist/physicist: this bot engages with the user on science or physics-related questions.

- GAI-life_coach/psychiatrist: this bot is aimed at improving the mental well being of the user, helping the user make progress to attain greater fulfillment in life, provides advice related to interpersonal relationships, uses psychiatric techniques to help the user, and works the user through stressful or emotionally complex situations.

If desired, the user can manually specify which bot they want to speak with.

# Voice Commands

- "please connect to a/an {specialist}": This will route you to an appropriate assistant. If you don't know what you're looking for, give information on the skills you need and you'll be routed accordingly.
- "please reboot system": this deletes all assistants and recreates them.
- "please display last message": this shows the message you sent to the bot previously.
- "please clear memory": this will start a new conversation thread, effectively wiping the assistant's memory.
- "please clear all memory": this will wipe all asisstants' memory.
- "please toggle text to speech": this will toggle betweeen OpenAI's TTS and the on-device TTS. OpenAI is used by default.


# Installation

1. Clone to computer
2. Modify the [GPT.kt](https://github.com/Adri6336/GPT-Assistants-Interlink/blob/main/app/src/main/java/com/example/gpt_assistants_interlink/presentation/GPT.kt) file to have your API key as the OPENAI_KEY constant at the top of the file.
3. Open project in Android Studio and build APK.
4. Locate APK and use adb to install on your watch (make sure the watch has developer options enabled)

# Customization After Installation

You can manipulate the assistants from your OpenAI playground, changing the model, prompt, and adding features as desired. As long as you do not request deletion in the app, these should persist. 
