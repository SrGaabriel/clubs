![GitHub Workflow Status](https://img.shields.io/github/workflow/status/SrGaabriel/clubs/Build)
![GitHub issues](https://img.shields.io/github/issues/SrGaabriel/clubs)
![GitHub Repo stars](https://img.shields.io/github/stars/SrGaabriel/clubs)

# ♣️ clubs

A simple and versatile command framework to improve command experience in `Guilded` in an elegant way.

The default implementation of the `common` module is the `bot` one, which uses [deck](https://github.com/SrGaabriel/deck/) to communicate with Guilded's Bot API.

## Documentation

There's no official documentation, but you can find some examples below.

#### common

This is module containing the main functionalities of the framework, being less powerful than the others:

```kotlin
val command = newCommand("repeat", "repeattask") {
    // Integer type
    val times by integer("times").required()
    // Nullable string type, only accepts a word
    val exampleWord by word("word").optional()
    // Nullable string type, only accepts text between quotes ("")
    val exampleQuotedString by quote("quote").optional()
    // Nullable string type, accepts unlimited words, but must be the last argument
    val exampleGreedyString by text("greedy_string").optional()
    runs {
        repeat(times) {
            send("$times")
            send(exampleWord)
            send(exampleQuotedString)
            send(exampleGreedyString)
        }
    }
    // Subcommand
    command("massive") {
        val times by long("times").required()
        runs {
            repeat(times) { count ->
                 send("Count: $count")
            }
        }
    }
}
```

You can't use this module alone, since it does not have a command handler, in other words, it doesn't have a bridge to send and receive content from, so it can't reply nor parse commands.

#### bot

This module is the implementation of clubs to the official Guilded Bot API. You must be in the early access program to be able to use this properly. Usage:

```kotlin
val command = command("repeat") {
    val times by integer("times").required()
    val message by quote("message").optional()
    runs {
        repeat(times) {
            channel.sendMessage(message ?: "Message was not specified.")
        }
    }
}
```

Unfortunately, this module doesn't support specific-platform types, such as `channels`, `users` or `roles`. Because of the early stage of the bot API, these entities can't be parsed as arguments yet.

#### client

This module is deprecated and probably won't be receiving new features. Please refer to the [bot](https://github.com/SrGaabriel/clubs#client) module. Anyway, here is an example:

```kotlin
val command = command("hello") {
    val user by user("user").required()
    val role by role("role").required()
    val channel by channel("channel").required()
    runs {
        this.channel.sendContent {
            paragraph {
                + "Channel: "
                + channel
            }
            paragraph {
                + "User: "
                + user
            }
            paragraph {
                + "Role: "
                + role
            }
        }
    }
}
```

The above example accepts user, role and channel mentions and sends a reply mentioning them again.

If you want to get the channel's state (like name, topic etc...), just like with `deck`, you can use `channel.getState()` to fetch this data making a request to guilded's API.

## Implementation

To use `clubs` in your project, you only need to add this to your `build.gradle.kts`:

```kotlin
repositories {
    maven("https://jitpack.io/")
}
```

Then in your dependencies:

```kotlin
dependencies {
    implementation("com.github.srgaabriel.clubs:clubs-client:$clubsVersion")
}
```

Here you can replace `clubsVersion` with the latest version (`0.6-SNAPSHOT`).

And if you only want to use the `common` module, you can just replace `clubs-client` with `clubs-common`.

## Thanks

I got inspiration from [Hexalite](https://github.com/HexaliteNetwork/java-edition-network), a project I'm working on with [eexsty](https://github.com/eexsty/) and some other amazing devs!