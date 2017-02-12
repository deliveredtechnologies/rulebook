#!/usr/bin/env bash
openssl aes-256-cbc -K $encrypted_387a20842b2b_key -iv $encrypted_387a20842b2b_iv -in travis/mavenkey.asc.enc -out travis/mavenkey.asc -d
gpg --fast-import travis/mavenkey.asc