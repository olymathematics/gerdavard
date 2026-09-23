MVNW ?= mvn

.PHONY: all generate build test verify run clean

all: verify

generate:
	$(MVNW) generate-sources

build:
	$(MVNW) package -DskipTests

test:
	$(MVNW) test

verify:
	$(MVNW) verify

run:
	$(MVNW) spring-boot:run

clean:
	$(MVNW) clean
