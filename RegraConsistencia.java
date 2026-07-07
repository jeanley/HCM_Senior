package custom.senior.apuracao;

import com.senior.ContextoGeralRH;
import com.senior.entity.IContaBH;
import com.senior.rule.Rule;
import java.time.LocalDate;
import java.util.List;

import custom.senior.apuracao.acerto.ContextoConsistenciaAcerto;

@Rule(description = "Regra Consistência de Acertos")
public class RegraConsistencia extends ConsistenciaAcertos {

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		ContextoGeralRH  CxGeral = getContainer().getContextoGeral();
		ContextoConsistenciaAcerto ace = getContainer().getContextoConsistenciaAcerto();
		
		//Verificar se tem conta de banco
		int NumEmp = ace.getColaborador().getNumEmp();
		int TipCol = ace.getColaborador().getTipCol();
		int NumCad = ace.getColaborador().getNumCad();
		LocalDate DatPro = ace.getData();
		
		List<IContaBH> contas = CxGeral.buscaContas(NumEmp, TipCol, NumCad, DatPro);
		int TemBhr = contas.size();
		
		if((TemBhr == 0) && 
		  ((ace.getHorSit(400) > 0) || 
		   (ace.getHorSit(401) > 0) || 
		   (ace.getHorSit(402) > 0) ||
		   (ace.getHorSit(403) > 0) ||
		   (ace.getHorSit(408) > 0) ||
		   (ace.getHorSit(409) > 0) ||
		   (ace.getHorSit(410) > 0) ||
		   (ace.getHorSit(411) > 0))){
			ace.mensagemLog("COLABORADOR SEM CONTA DE BANCO! NÃO É PERMITIDO GERAR CRÉDITO E DÉBITO!");
		}

		

	}

}
