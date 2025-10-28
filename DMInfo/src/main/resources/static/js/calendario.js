const PALETA_CORES = [
    '#d90429',
    '#007f5f',
    '#023e8a',
    '#f77f00',
    '#6a040f',
    '#736eb1',
    '#00b4d8',
    '#2add70'
];

function transformarAtividadeParaEvento(atividade) {
    let titulo;
    let idTipoAtividade;

    if (atividade.atv && atividade.atv.descricao)
        titulo = atividade.atv.descricao;
    else
        titulo = 'Atividade (sem nome)';

    if (atividade.atv)
        idTipoAtividade = atividade.atv.id;
    else
        idTipoAtividade = 0;

    const dataInicio = atividade.dtIni;
    const horaInicio = atividade.horario || '00:00:00';
    const startDateTime = `${dataInicio}T${horaInicio}`;

    const dataFim = atividade.dtFim || atividade.dtIni;
    const horaFim = '23:59:00';
    const fimDataHora = `${dataFim}T${horaFim}`;

    const corEvento = PALETA_CORES[idTipoAtividade % PALETA_CORES.length];

    return {
        id: atividade.id,
        title: titulo,
        start: startDateTime,
        end: fimDataHora,
        allDay: false,
        color: corEvento,
        borderColor: corEvento,
        textColor: '#ffffff'
    };
}

document.addEventListener('DOMContentLoaded', async () => {
    const calendarioElemento = document.getElementById('calendario');

    const calendario = new FullCalendar.Calendar(calendarioElemento, {
        initialView: 'dayGridMonth',
        locale: 'pt-br',
        eventDisplay: 'block',
        showNonCurrentDates: false,
        eventTimeFormat: {
            hour: '2-digit',
            minute: '2-digit',
            hour12: false
        },
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,timeGridDay'
        },
        buttonText: {
            today: 'Hoje',
            month: 'Mês',
            week: 'Semana',
            day: 'Dia'
        },

        events: async function(info, sucesso, falha) {
            try {
                const resposta = await fetch('/finalizar-atividades');
                if (!resposta.ok) {
                    throw new Error('Falha ao buscar atividades do backend.');
                }
                const atividadesJson = await resposta.json();
                const eventos = atividadesJson.map(transformarAtividadeParaEvento);
                sucesso(eventos);
            } catch (error) {
                console.error("Erro ao carregar eventos:", error);
                falha(error);
                alert("Não foi possível carregar as atividades no calendário.");
            }
        },

        dateClick: function(info) {
            calendario.changeView('timeGridDay', info.dateStr);
        },

        eventClick: function(info) {
            const inicio = info.event.start.toLocaleString('pt-BR', {
                day: '2-digit', month: '2-digit', hour: '2-digit', minute: '2-digit'
            });
            const fim = info.event.end.toLocaleString('pt-BR', {
                day: '2-digit', month: '2-digit', hour: '2-digit', minute: '2-digit'
            });
            alert(
                `Atividade: ${info.event.title}\n\n` +
                `Início: ${inicio}h\n` +
                `Fim: ${fim}h`
            );
        },

        eventContent: function(arg) {
            let conteudoHtml = '';

            if (arg.isStart && arg.timeText)
                conteudoHtml += `<span class="fc-event-time">${arg.timeText}</span> `;

            conteudoHtml += `<span class="fc-event-title">${arg.event.title}</span>`;

            return {
                html: conteudoHtml
            };
        }
    });
    calendario.render();
});