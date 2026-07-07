select	a.numemp,
		a.codfil,
		a.numcad,
		a.nomfun,
		convert(varchar(10), a.datadm,103) as datadm,
		a.numcpf,
		a.tipsex,
		convert(varchar(10), a.datnas,103) as datnas,
		a.estciv,
		f.valkey as estadoCivil,
		a.codcar,
		c.titcar,
		b.codvin,
		d.desvin,
		e.nomdep,
		(g.hormes/60) as horasMensais,
		(g.horsem/60) as horasSemanais,
		cast(h.dddtel as varchar) + cast(h.numtel as varchar) as telefone,
		(h.endrua + ', ' + h.endnum) as endereco,
		i.nomcid as cidadeEndereco,
		j.codest as estadoNascimento
from	r034fun a
		left join r038hvi (nolock) b on b.numemp = a.numemp and b.tipcol = a.tipcol and b.numcad = a.numcad
                            and b.datalt = (select max(datalt) from r038hvi hsi where hsi.numemp = a.numemp and hsi.tipcol = a.tipcol and hsi.numcad = a.numcad and hsi.datalt <= getdate())
		inner join r024car c on c.estcar = a.estcar and c.codcar = a.codcar
		left join r022vin d on d.codvin = b.codvin
		left join r036dep e on e.numemp = a.numemp and e.tipcol = a.tipcol and e.numcad = a.numcad and e.grapar = 3 and e.tipsex = 'F'
		left join r996lsf f on f.keynam	= a.estciv and f.lstnam = 'lEstciv'
		left join r006esc g on g.codesc = a.codesc
		inner join r034cpl h on h.numemp = a.numemp and h.tipcol = a.tipcol and h.numcad = a.numcad 
		left join r074cid i on i.codcid = h.codcid
		left join r074est j on j.codest = h.estnas
where	a.tipcol = 1
and		a.sitafa not in (7,18)
and		a.numemp in (3,4)
order by a.numemp, a.codfil, a.nomfun



--select top 10 * from r996lsf
--Precisamos de um relatório com os dados: ]
--Unidade_
--matrícula_
--Nome_
--CPF_
--sexo_
--Cargo_
--data de nascimento
--_nome da mães_
--estado civil_
--data de admissão_
--Vínculo do cargo(de é determinado...
--carga horária mensal_
--carga horária semanal_
--telefone_
--endereço_
--Estado de nascimento.
