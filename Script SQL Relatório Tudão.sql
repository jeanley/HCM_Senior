Empresa, Filial, Descrição Filial, CNPJ Filial, Matrícula, Nome Completo, Cargo, tempo no ultimo cargo, Tempo de casa, Sexo, Data de Admissão, Data de Rescisão, Situação, Salário base, carga horária, Tipo de Vínculo, Tipo de Contrato, CPF, Código do Sindicato, Descrição do Sindicato, Indicador de Deficiência (S/N), Tipo de deficiência, preenche cota de deficiência, data de nascimento, nacionalidade, modo de pagamento, tipo de conta, período de pagamento, Banco, Agencia, Conta, Digito, PIS, RG,  telefone, e-mail, Logradouro, Endereço, Numero, CEP, Bairro, Cidade, País, e-mail, Escolaridade, Raça/Cor, Estado civil,  cód. posto de trabalho, escala, turma, estabilidade, local, centro de custo, controle de ponto, duração do contrato, nome completo mãe, nome completo pai, dependentes, dependentes para IRRF, dependentes para salário família, motivo do ultimo afastamento, data do ultimo afastamento, data de retorno do ultimo afastamento


SELECT T1.NumEmp as Empresa, T1.CodFil as CodigoFilial, T2.NomFil as Filial, T2.numcgc as CNPJ, T1.TipCol as Tipo, T1.NumCad as Marticula, T1.NomFun as Nome, T1.CodCar as CodigoCargo, T3.TitCar as Cargo, T4.DatAlt
FROM r034fun T1
LEFT JOIN R030FIL (nolock) T2 on T2.NumEmp = T1.NumEmp and T2.CodFil = T1.CodFil
LEFT JOIN R024CAR (nolock) T3 on T3.EstCar = T1.EstCar and T3.CodCar = T1.CodCar
LEFT JOIN(SELECT NumEmp, TipCol, NumCad, EstCar, CodCar, MAX(DATALT) as DatAlt FROM R038HCA GROUP BY NumEmp, TipCol, NumCad, EstCar, CodCar) T4 on T4.NumEmp = T1.NumEmp and T4.TipCol = T1.TipCol and T4.NumCad = T1.NumCad and T4.EstCar = T1.EstCar and T4.CodCar = T1.CodCar
where T1.NumCad = 20010893
