#!/usr/bin/env bash
openssl aes-256-cbc -K $encrypted_387a20842b2b_key -iv $encrypted_387a20842b2b_iv -in mavenkey.asc.enc -out mavenkey.asc -d
gpg --fast-import travis/mavenkey.asc
