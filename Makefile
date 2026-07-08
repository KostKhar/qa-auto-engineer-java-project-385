# Homebrew OpenJDK 21 (install: brew install openjdk@21)
BREW_PREFIX := $(shell brew --prefix openjdk@21 2>/dev/null)
ifneq ($(BREW_PREFIX),)
export JAVA_HOME := $(BREW_PREFIX)/libexec/openjdk.jdk/Contents/Home
export PATH := $(JAVA_HOME)/bin:$(PATH)
endif

.DEFAULT_GOAL := build-run

setup:
	cd code &&  ./gradlew wrapper --gradle-version 8.13

clean:
	cd code &&  ./gradlew clean

build:
	cd code &&  ./gradlew clean build

install:
	cd code && ./gradlew  clean installDist --no-daemon

start:
	docker run --rm -p 5173:5173 hexletprojects/qa_auto_java_testing_kanban_board_project_ru_app

run:
	cd code && ./gradlew run

test:
	cd code && ./gradlew test

report:
	cd code && ./gradlew jacocoTestReport

lint:
	cd code && ./gradlew spotlessApply

update-deps:
	cd code && ./gradlew refreshVersions


build-run: build run

.PHONY: build