![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/SrGaabriel/clubs/gradle.yml?branch=development&style=for-the-badge)
![GitHub issues](https://img.shields.io/github/issues/SrGaabriel/clubs?style=for-the-badge)
![GitHub Repo stars](https://img.shields.io/github/stars/SrGaabriel/clubs?style=for-the-badge)

# ♣️ clubs

A simple and versatile command framework to improve command experience in `Guilded` elegantly.

The default and built-in implementation of the `common` module is `:bot`, which uses [deck](https://github.com/SrGaabriel/deck/) to communicate with Guilded's Bot API.

## Usage

There's no official documentation, but you can find some examples below.

#### common

This module contains the main functionalities of the framework, being less powerful than the others:

**(NEW!)** Delegated arguments syntax:

```kotlin
// Possible patterns of execution: <command> [message] [times] | [times]
val command = genericCommand("repeat", "repeattask") {
    val message by optionalArgument("message", quote())
    val times by requiredArgument("times", integer())
    executor {
        repeat(times) { count ->
            send("${message ?: "No message specified."} (x${count+1})")
        }
    }
}
```

Normal syntax:

```kotlin
val command = genericCommand("repeat", "repeattask") {
    argument("times", integer()) { times ->
        executor {
            repeat(times.infer()) { count ->
                send("No message specified (x$count)")
            }
        }
        argument("message", phrase()) { message ->
            executor {
                repeat(times.infer()) { count ->
                    send(message.infer())
                }
            }
        }
    }
}
```

This module can't operate on its own, since it does not have a command handler. In other words, it doesn't have a bridge to send AND/OR receive content.

#### bot

This module is a Clubs implementation that uses the official Guilded Bot API. Usage:

```kotlin
val command = command("say") {
    val message by delegateArgument("message", quote())
    executor {
        channel.sendMessage(message)
    }
    literal("embed") {
        executor {
            channel.sendEmbed {
                description = message
            }
        }
    }
}
```

Example usage: `say "Hello, World!" embed`.

Unfortunately, this module doesn't support specific-platform types, such as `channels`, `users` or `roles` due to the early stage of the bot API **(at the time I developed this project)**.

## Implementation

To use clubs you just need to add the desired artifact inside your dependencies scope, for example: 

```kotlin
dependencies {
    implementation("io.github.srgaabriel.clubs:clubs-bot:$clubsVersion")
}
```

Here you can replace `clubsVersion` with the latest version (`1.4`).

**Note:** If you wish to use the `common` module alone, you can just replace `clubs-bot` with `clubs-common`.
