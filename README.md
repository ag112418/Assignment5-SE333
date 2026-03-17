# Assignment 5 – SE333 Software Engineering

![CI](https://github.com/ag112418/Assignment5-SE333/actions/workflows/SE333_CI.yml/badge.svg)

## Project Overview
This project implements a simplified Amazon-style checkout system written in Java.  
The system calculates the final cost of items in a shopping cart by applying different pricing rules.

The main components include:
- **Amazon** – Main class responsible for calculating the total cost.
- **ShoppingCartAdaptor** – Handles cart operations and interacts with the database.
- **Database** – In-memory HSQLDB database used for storing cart items.
- **Item** – Represents products added to the cart.

### Pricing Rules
The system applies multiple pricing rules using the `PriceRule` interface:

- **RegularCost**
  - Calculates the base cost of items (`pricePerUnit × quantity`)
- **DeliveryPrice**
  - Delivery cost tiers:
    - 0 items → $0
    - 1–3 items → $5
    - 4–10 items → $12.50
    - >10 items → $20
- **ExtraCostForElectronics**
  - Adds an additional **$7.50** if an electronic item is present in the cart

## Testing
This project includes both **unit tests** and **integration tests**.

### Unit Tests
`AmazonUnitTest`
- Tests individual components using **Mockito**
- Verifies:
  - `Amazon.calculate()` aggregates all `PriceRule` implementations
  - `Amazon.addToCart()` correctly calls the shopping cart

### Integration Tests
`AmazonIntegrationTest`
- Uses the real **HSQLDB in-memory database**
- Tests multiple components working together
- Verifies:
  - Regular cost calculations
  - Delivery price tiers
  - Extra electronics cost
  - Empty cart behavior

Testing frameworks used:
- **JUnit 5**
- **Mockito**
- **AssertJ**

## Continuous Integration (GitHub Actions)

This repository uses **GitHub Actions** to automatically run tests and analysis on every push to the `main` branch.

The workflow performs the following steps:

1. Runs **Checkstyle static analysis** during the `validate` phase
2. Executes **unit and integration tests**
3. Generates **JaCoCo code coverage reports**
4. Uploads analysis reports as build artifacts

## Reports Generated

The workflow produces the following artifacts:

### Checkstyle Report
Static code analysis results

Artifact digest: sha256:d9083d89b303b2fc1a46e62a947884ea41df8559e672631d13de2c790be11778 


### JaCoCo Coverage Report
Code coverage results for all tests

Artifact digest: sha256:824b0a56556efb604381e4061f1e40f6e8667c0302e56000b02ce57e4aa47fca


Both reports are available in the **Artifacts section of the GitHub Actions workflow run**.

## Technologies Used

- Java
- Maven
- JUnit 5
- Mockito
- AssertJ
- HSQLDB
- JaCoCo
- Checkstyle
- GitHub Actions

## Workflow Status

The CI pipeline successfully runs:
- Static analysis
- Unit tests
- Integration tests
- Coverage generation

A green build badge indicates that all checks passed successfully.