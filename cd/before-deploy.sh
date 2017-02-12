#!/usr/bin/env bash
openssl aes-256-cbc -K $encrypted_35e61015f178_key -iv $encrypted_35e61015f178_iv -in travis_maven_code_signing.asc.enc -out travis_maven_code_signing.asc -d
gpg --fast-import cd/signingkey.asc
CD