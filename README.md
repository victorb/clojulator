# clojulator

Calculator made using Clojurescript + Om. Runs in the browser, iOS and Android.

## Overview

This is a project meant for learning clojure/clojurescript. Not really meant to build a full-scale project,
just something to get a taste of clojurescript and Om.

![](screenshot.png)

## Setup

To get an interactive development environment run:

    lein figwheel

and open your browser at [localhost:3449](http://localhost:3449/).
This will auto compile and send all changes to the browser without the
need to reload. After the compilation process is complete, you will
get a Browser Connected REPL. An easy way to try it is:

    (js/alert "Am I connected?")

and you should see an alert in the browser window.

To clean all compiled files:

    lein clean

To create a production build run:

    lein do clean, cljsbuild once min

And open your browser in `resources/public/index.html`. You will not
get live reloading, nor a REPL. 

## License

Copyright © 2016 Victor Bjelkholm MIT
