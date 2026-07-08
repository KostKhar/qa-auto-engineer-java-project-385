# Homebrew OpenJDK 21 (install: brew install openjdk@21)
BREW_PREFIX := $(shell brew --prefix openjdk@21 2>/dev/null)
ifneq ($(BREW_PREFIX),)
export JAVA_HOME := $(BREW_PREFIX)/libexec/openjdk.jdk/Contents/Home
export PATH := $(JAVA_HOME)/bin:$(PATH)
endif

.DEFAULT_GOAL := build-run

setup:
	./gradlew wrapper --gradle-version 8.13

clean:
	./gradlew clean

build:
	./gradlew clean build

install:
	./gradlew clean installDist --no-daemon

start:
	docker run --rm -p 5173:5173 hexletprojects/qa_auto_java_testing_kanban_board_project_ru_app

run:
	./gradlew run

test:
	./gradlew test

report:
	./gradlew jacocoTestReport

lint:
	./gradlew spotlessApply

update-deps:
	./gradlew refreshVersions


build-run: build run

.PHONY: build