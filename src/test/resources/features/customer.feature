Feature: CRUD de Clientes

  Background:
    Given que a API esta rodando

  Scenario: Criar cliente com sucesso
    When eu envio POST para "/api/v1/customers" com:
      """
      {
        "name": "Erik Santos",
        "cpf": "52998224725",
        "email": "erik@test.com",
        "phone": "11999999999"
      }
      """
    Then o status deve ser 201
    And o header "Location" deve conter "/api/v1/customers/"

  Scenario: Buscar cliente criado
    When eu envio POST para "/api/v1/customers" com:
      """
      {
        "name": "Joao Silva",
        "cpf": "11144477735",
        "email": "joao.silva@test.com",
        "phone": "11988887777"
      }
      """
    Then o status deve ser 201
    And o header "Location" deve conter "/api/v1/customers/"
    When eu envio GET para "/api/v1/customers/{id}"
    Then o status deve ser 200

  Scenario: Nao permite CPF duplicado
    Given existe um cliente com CPF "14538220620"
    When eu envio POST para "/api/v1/customers" com:
      """
      {
        "name": "Outro Cliente",
        "cpf": "14538220620",
        "email": "outro145@test.com",
        "phone": "11977776666"
      }
      """
    Then o status deve ser 400
    And a resposta contem "Ja existe cliente cadastrado com o CPF"

  Scenario: Nao permite email duplicado
    Given existe um cliente com email "duplicado@test.com"
    When eu envio POST para "/api/v1/customers" com:
      """
      {
        "name": "Email Duplicado",
        "cpf": "15350946056",
        "email": "duplicado@test.com",
        "phone": "11966665555"
      }
      """
    Then o status deve ser 400
    And a resposta contem "Ja existe cliente cadastrado com o email"

  Scenario: CPF invalido retorna erro
    When eu envio POST para "/api/v1/customers" com:
      """
      {
        "name": "CPF Invalido",
        "cpf": "11111111111",
        "email": "cpfinvalido@test.com",
        "phone": "11955554444"
      }
      """
    Then o status deve ser 400
    And a resposta contem "CPF invalido"

  Scenario: Listar clientes com filtro
    Given existem 3 clientes cadastrados
    When eu envio GET para "/api/v1/customers?search=joao"
    Then o status deve ser 200
    And a lista contem pelo menos 1 cliente

  Scenario: Atualizar cliente com CPF ja existente de outro cliente
    Given existe cliente A com CPF "52998224725"
    And existe cliente B com ID conhecido
    When eu envio PUT para "/api/v1/customers/{id}" com:
      """
      {
        "name": "Cliente B Atualizado",
        "cpf": "52998224725",
        "email": "clienteb.atual@test.com",
        "phone": "11944443333"
      }
      """
    Then o status deve ser 400
    And a resposta contem "Ja existe cliente cadastrado com o CPF"

  Scenario: Soft delete funciona
    Given existe um cliente com ID conhecido
    When eu envio DELETE para "/api/v1/customers/{id}"
    Then o status deve ser 204
    When eu envio GET para "/api/v1/customers/{id}"
    Then o status deve ser 404

  Scenario: Campos obrigatorios
    When eu envio POST para "/api/v1/customers" com:
      """
      {
        "cpf": "52998224725"
      }
      """
    Then o status deve ser 400
    And a resposta contem "Dados invalidos"
