# Frontend for the profile id service

Frontend scaffold implemented with Scala JS + scalm, which provides an Elm-like architecture for Scala JS.

## Prerequisites

Install [node.js](https://nodejs.org/en/download/).

## Compilation

Compile with:

    sbt fastOptJS::webpack

#### For production

    sbt fullOptJS::webpack
    
## Run local dev server

From the sbt shell:

    fastOptJS::startWebpackDevServer

The server will run on [localhost:8080](localhost:8080)

Instruct SBT to rebuild on source changes:

    ~fastOptJS::webpack

Shut down dev server with

    fastOptJS::stopWebpackDevServer





