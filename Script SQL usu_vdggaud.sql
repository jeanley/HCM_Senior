select	getdate() as dataReferencia,
        a.numemp as codigoEmpresa,
		a.numcad as Matricula,  
		a.numcpf as CPF,  
		upper(a.nomfun) as NomeCompleto,  
		a.datnas as dataNascimento,
		a.codcar as codigoCargo,
		upper(c.titcar) as CargoFuncao,
		c.codcb2 as Cbo,
		a.datadm as DataAdmissao, 
		a.deffis as deficiencia,
		a.codccu as codCentroCusto,
		j.nomccu as nomeCentroCusto,
		upper(d.dessit) as Status,
		case 
			when a.sitafa in (7,18) then y.desdem
			else null
		end as motivoDesligamento,
		case  
			when h.sitafa is not null then h.datafa  
			else null  
		end as dataDesligamento,
		case
			when a.sitafa not in (1,7,18) then hh.datafa
			else null
		end as dataAfastamento,
		a.codfil as Unidade,
		i.nomfil as nomeUnidade,
		q.valkey as regional,
		b.numtel as Telefone,  
		upper(b.endrua) + ', ' + b.endnum as enderecoResidencia,  
		upper(e.nombai) as bairroResidencia,  
		upper(f.nomcid) as cidadeResidencia,  
		b.endcep as cepResidencia,
		b.estcid as estadoMoradia,
		b.endcpl as complementoResidencia,
		k.nomcid as cidadeNascimento,
		k.estcid as estadoNascimento,
		b.emapar as emailParticular,
		b.emacom as emailCorporativo,
		g.matriculaGestor,
		g.nomeGestor,  
		g.cargoGestor,
		a.tipsex as genero,
		l.valkey as estadoCivil,
		m.desnac as nacionalidade,
		a.postra as postoColaborador,
		n.valkey as racaEtnia,
		p.nomsin as nomeSindicato,
		o.codsin as codigoSindicato,
		p.mesdis as dataBaseSindicato,
		r.codvin as codigoVinculo,
		s.desvin as nomeVinculo,
		cast(a.valsal as decimal(9,2)) as salario,
		upper(t.desgra) as escolaridade,
		(u.hormes/60) as cargaHoraria,
		case
			when (a.coddef is null or a.coddef = 0) then null
			else upper(v.desdef)
		end as tipoDeficiencia
from	r034fun (nolock) a   
		inner join r034cpl b on b.numemp = a.numemp and b.tipcol = a.tipcol and b.numcad = a.numcad  
		left join r024car c on c.estcar = a.estcar and c.codcar = a.codcar  
		left join r010sit d on d.codsit = a.sitafa  
		left join r074bai e on e.codbai = b.codbai and e.codcid = b.codcid  
		left join r074cid f on f.codcid = b.codcid  
		left join usu_vhiecol g on g.codigoEmpresaColaborador = a.numemp and g.tipoColaborador = a.tipcol and g.matriculaColaborador = a.numcad  
		left join r038afa h on h.numemp = a.numemp and h.tipcol = a.tipcol and h.numcad = a.numcad and h.sitafa in (7,18)
		left join r038afa hh on hh.numemp = a.numemp and hh.tipcol = a.tipcol and hh.numcad = a.numcad and hh.sitafa = a.sitafa
							and hh.datafa = (select max(datafa) from r038afa afa where afa.numemp = a.numemp and afa.tipcol = a.tipcol and afa.numcad = a.numcad and afa.sitafa = a.sitafa)
		left join r030fil i on i.codfil = a.codfil and i.numemp = a.numemp  
		left join r018ccu j on j.numemp = a.numemp and j.codccu = a.codccu
		left join r074cid k on k.codcid = b.ccinas
		left join r996lsf l on l.keynam = a.estciv and l.lstnam = 'lEstciv'
		left join r023nac m on m.codnac = a.codnac
		left join r996lsf n on n.keynam = a.raccor and n.lstnam = 'lraccor'
		left join r038hsi o on o.numemp = a.numemp and o.tipcol = a.tipcol and o.numcad = a.numcad
		                    and o.datalt = (select max(datalt) from r038hsi hsi where hsi.numemp = a.numemp and hsi.tipcol = a.tipcol and hsi.numcad = a.numcad)
		left join r014sin p on p.codsin = o.codsin
		left join r996lsf q on q.keynam = i.usu_region and q.lstnam = 'usu_lRegion'
		left join r038hvi (nolock) r on r.numemp = a.numemp and r.tipcol = a.tipcol and r.numcad = a.numcad
                            and r.datalt = (select max(datalt) from r038hvi hsi where hsi.numemp = a.numemp and hsi.tipcol = a.tipcol and hsi.numcad = a.numcad and hsi.datalt <= getdate())
        left join r022vin s on s.codvin = r.codvin
		left join r022gra t on t.grains = a.grains
		left join r006esc u on u.codesc = a.codesc
		left join r022def v on v.coddef = a.coddef
		left join r042rcm x on x.numemp = a.numemp and x.tipcol = a.tipcol and x.numcad = a.numcad
		left join r042cau y on y.caudem = x.caudem
where	1 = 1  
and		a.tipcol = 1  
and		a.numemp < 5  
and		( (a.sitafa not in (7,18) ) or ( a.sitafa in(7,18) and h.datafa > getDate() - 90) )



