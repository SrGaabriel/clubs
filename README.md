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

You can't use this module alone, since it does not have a command handler, in other words, it doesn't have a bridge to send and receive content from, so it can't reply nor parse commands.

#### bot

This module is the implementation of clubs to the official Guilded Bot API. You must be in the early access program to be able to use this properly. Usage:

```kotlin
val command = command("say") {
    argument("message", quote()) { message ->
        executor {
            channel.sendMessage(message.infer())
        }
        literal("embed") {
            executor {
                channel.sendEmbed {
                    description = message.infer()
                }
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

Here you can replace `clubsVersion` with the latest version (`1.1.4`).

If you only wish to use the `common` module, you can just replace `clubs-bot` with `clubs-common`.