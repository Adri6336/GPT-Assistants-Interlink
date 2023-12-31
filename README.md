# GPT-Assistants-Interlink

GPT-Assistants-Interlink is a mobile assistant app optimized for your Android smartwatch that takes advantage of OpenAI's API, using its Assistants, Whisper, Chat, and TTS tools to empower your device. This application is engineered to enhance your daily life by providing responsive and personalized virtual assistance and companionship on the fly, using a suite of specialized AI instances tailored for various tasks.

Optimized for the Ticwatch Pro 3 Ultra.

## Overview

The application empowers your smartwatch by enabling easy access to a range of specialist AI assistants designed to address your needs effectively. With a simple message to the interlink, it intelligently connects you to the most suitable assistant based on your query.

![GPT-Assistants-Interlink Interface](https://github.com/Adri6336/GPT-Assistants-Interlink/assets/64619524/78d1c782-f47e-41c4-8b3f-3e77bfb0af59)

Upon initiation, the app generates an array of specialist assistants, which are automatically regenerated in case of memory loss. Code interpreter functionality is available for assistants requiring it.

Conversations flow seamlessly with your chosen assistant and switch only on explicit requests to connect with a different specialist.

### Specialists Available

- **GAI-translator**: Specializes in translating content between English and other languages or vice versa.
  
- **GAI-generalist**: A jack-of-all-trades assistant for general inquiries and memory storing.

- **GAI-engineer/mechanic**: Dedicated to topics concerning devices, software, machinery, and tools.

- **GAI-friend**: Offers companionship and casual conversation for users seeking social interaction.

- **GAI-advisor**: Provides pragmatic and sage advice on complex matters to enhance the user's well-being.

- **GAI-maths/accounting**: Assists with mathematical and financial computations or queries.

- **GAI-scientist/physicist**: Engages users with discussions or questions related to science and physics.

- **GAI-life_coach/psychiatrist**: Aims to support mental health, personal growth, relationship advice, and navigating emotional challenges. Will ask questions to better help user with tailored counseling.

Users may specify their preferred assistant manually if necessary.

## Voice Commands

Here's how you can interact with GPT-Assistants-Interlink using voice commands:

- **"Please connect to a/an {specialist}":** Directs the system to link you to a specialist.
- **"Please reboot system":** Resets and regenerates all assistant instances.
- **"Please display last message":** Retrieves the most recent communication from the bot.
- **"Please clear memory":** Initiates a new conversation thread, clearing the current assistant's memory.
- **"Please clear all memory":** Resets the memory across all assistant instances.
- **"Please toggle text to speech":** Switches between OpenAI's TTS and the watch's native TTS. The default setting uses OpenAI's TTS.

## Installation Guide

1. Clone the repository to your computer.
2. Update the [GPT.kt](https://github.com/Adri6336/GPT-Assistants-Interlink/blob/main/app/src/main/java/com/example/gpt_assistants_interlink/presentation/GPT.kt) file with your specific OpenAI API key.
3. Launch the project in Android Studio and compile the APK.
4. Transfer and install the APK onto your watch using adb (Developer options should be activated on the watch).

## Customization Post-Installation

Post-installation, you can modify the assistants within your OpenAI sandbox by altering the model, prompt, and features as you see fit. These customizations will remain intact unless deletion is initiated through the application.

Make the most of your day with GPT-Assistants-Interlink, your smartwatch's ultimate virtual sidekick.


## Personalization Setup

As you begin using GPT Assistants Interlink, you will encounter a personalization setup that is instrumental for tailoring the AI to your unique communication style. Follow these simple steps to ensure the best possible customization:

1. Start off by tapping the screen to activate the introduction sequence.
2. Record a message, expressing yourself just as you typically would—if you're someone who frequently uses slang or technical jargon, don't hold back. In this message, tell the machine a bit about yourself, such as your name, interests, and other meaningful info.
3. Conclude your introduction by tapping the screen once again.

Your recorded introduction plays a critical role in calibrating the AI. It analyzes your speech patterns, vocabulary, and preferred expressions to adapt its interactions with you. This means that the more authentic and detailed you are during this phase, the better the AI can align with your conversational style.

Additionally, if you experience any neurodivergence such as ADHD, consider mentioning this in your introduction. It enables the AI to make adjustments in how it presents information, striving to deliver responses in a way that may cater to your cognitive preferences.

By engaging with the personalization setup thoughtfully, you set the stage for a more responsive and individualized experience with GPT Assistants Interlink. Your input here is invaluable for creating an AI companion that feels right at home with your way of communicating and interacts with you as a known unique person rather than just another random user.