# Homebrew OpenJDK 21 (install: brew install openjdk@21)
BREW_PREFIX := $(shell brew --prefix openjdk@21 2>/dev/null)
ifneq ($(BREW_PREFIX),)
export JAVA_HOME := $(BREW_PREFIX)/libexec/openjdk.jdk/Contents/Home
export PATH := $(JAVA_HOME)/bin:$(PATH)
endif

.DEFAULT_GOAL := build

APP_BASE_URL ?= http://localhost:5173/

setup:
	./gradlew wrapper --gradle-version 8.13

clean:
	./gradlew clean

start:
	docker run --rm -p 5173:5173 hexletprojects/qa_auto_java_testing_kanban_board_project_ru_app

build:
	./gradlew clean build -PAPP_BASE_URL=$(APP_BASE_URL)

test:
	./gradlew test

lint:
	./gradlew checkstyleMain checkstyleTest

.PHONY: setup clean start build test lint
