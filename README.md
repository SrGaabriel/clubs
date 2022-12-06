![GitHub Workflow Status](https://img.shields.io/github/workflow/status/SrGaabriel/clubs/Build)
![GitHub issues](https://img.shields.io/github/issues/SrGaabriel/clubs)
![GitHub Repo stars](https://img.shields.io/github/stars/SrGaabriel/clubs)

# ♣️ clubs

A simple and versatile command framework to improve command experience in `Guilded` in an elegant way.

The default implementation of the `common` module is the `bot` one, which uses [deck](https://github.com/SrGaabriel/deck/) to communicate with Guilded's Bot API.

## Documentation

There's no official documentation, but you can find some examples below.

#### common

This is module containing the main functionalities of the framework, it being the least powerful module:

**(NEW!)** Delegated arguments syntax:

```kotlin
val command = genericCommand("repeat", "repeattask") {
    // `Message` first, then `times` OR only `times`
    val message by optionalArgument("message", quote())
    val times by requiredArgument("times", integer())
    // `Times` only or `times` and `message`
    val times by requiredArgument("times", integer())
    val message by optionalArgument("message", quote())
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

This module doesn't do anything on its own, since it does not have a command handler. In other words, it doesn't have a bridge to send and receive content.

#### bot

This module is the implementation of clubs to the official Guilded Bot API. You must be in the early access program to be able to use this properly. Usage:

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

Unfortunately, this module doesn't support specific-platform types, such as `channels`, `users` or `roles`. Because of the early stage of the bot API, these entities can't be parsed as arguments yet.

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
    implementation("com.github.SrGaabriel.clubs:clubs-bot:$clubsVersion")
}
```

Here you can replace `clubsVersion` with the latest version (`1.3.11`).

If you wish to use the `common` module alone, you can just replace `clubs-bot` with `clubs-common`.