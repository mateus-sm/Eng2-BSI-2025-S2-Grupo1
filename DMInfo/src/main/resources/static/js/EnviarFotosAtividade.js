const msgFeedback = document.getElementById('msg-feedback');

async function carregarEventos() {
    const selectEvento = document.getElementById('id_evento');
    try {
        const response = await fetch('/apis/atividade/form/eventos');
        if (!response.ok) throw new Error('Falha ao carregar eventos');

        const eventos = await response.json();
        selectEvento.innerHTML = '<option value="" disabled selected>Selecione o Evento</option>';
        selectEvento.disabled = false;

        eventos.forEach(evento => {
            const option = document.createElement('option');
            option.value = evento.id;
            option.textContent = `${evento.titulo} (${evento.descricao})`;
            selectEvento.appendChild(option);
        });
    } catch (error) {
        console.error('Erro:', error);
        mostrarMensagem('Erro ao carregar eventos.', 'danger');
    }
}

async function carregarAtividades(idEvento) {
    const selectAtividade = document.getElementById('id_atividade');
    document.getElementById('galeria-container').classList.add('d-none');

    selectAtividade.innerHTML = '<option value="" disabled selected>Carregando atividades...</option>';
    selectAtividade.disabled = true;

    try {
        const response = await fetch(`/apis/atividade/form/eventos/${idEvento}/atividades`);
        if (!response.ok)
            throw new Error('Falha ao carregar atividades');

        const atividades = await response.json();
        selectAtividade.innerHTML = '<option value="" disabled selected>Selecione a Atividade</option>';
        selectAtividade.disabled = false;

        if (atividades.length === 0) {
            selectAtividade.innerHTML = '<option value="" disabled selected>Nenhuma atividade encontrada</option>';
            selectAtividade.disabled = true;
            return;
        }

        atividades.forEach(atividade => {
            const option = document.createElement('option');
            option.value = atividade.id;
            option.textContent = atividade.descricao;
            selectAtividade.appendChild(option);
        });
    } catch (error) {
        console.error('Erro:', error);
        mostrarMensagem('Erro ao carregar atividades.', 'danger');
    }
}

async function carregarFotos(idAtividade) {
    const galeriaContainer = document.getElementById('galeria-container');
    const listaFotos = document.getElementById('lista-fotos');
    const msgNoFotos = document.getElementById('no-fotos-msg');
    const loading = document.getElementById('loading-fotos');

    galeriaContainer.classList.remove('d-none');
    loading.classList.remove('d-none');
    listaFotos.innerHTML = '';
    msgNoFotos.classList.add('d-none');

    try {
        const response = await fetch(`/apis/atividade/${idAtividade}/fotos`);
        if (!response.ok)
            throw new Error('Erro ao buscar fotos');

        const fotos = await response.json();
        loading.classList.add('d-none');

        if (fotos.length === 0) {
            msgNoFotos.classList.remove('d-none');
            return;
        }

        fotos.forEach(foto => {
            const caminhoFoto = `/uploads/${foto.foto}?t=${new Date().getTime()}`;

            const card = document.createElement('div');
            card.className = 'foto-card';
            card.innerHTML = `
                <img src="${caminhoFoto}" class="foto-img" alt="Foto ${foto.id}" onclick="window.open('${caminhoFoto}', '_blank')">
                <button type="button" class="btn-delete-foto" onclick="excluirFoto(${foto.id}, ${idAtividade})" title="Excluir foto">
                    <i class="bi bi-trash-fill"></i>
                </button>
            `;
            listaFotos.appendChild(card);
        });

    } catch (error) {
        console.error(error);
        loading.innerHTML = 'Erro ao carregar fotos.';
    }
}

async function excluirFoto(idFoto, idAtividade) {
    if (!confirm('Tem certeza que deseja excluir esta foto?'))
        return;

    try {
        const response = await fetch(`/apis/fotos/${idFoto}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            const erro = await response.json();
            throw new Error(erro.erro || 'Erro ao excluir');
        }

        mostrarMensagem('Foto excluída com sucesso.', 'success');
        carregarFotos(idAtividade);

    } catch (error) {
        console.error(error);
        mostrarMensagem(error.message, 'danger');
    }
}

document.getElementById('foto').addEventListener('change', function(event) {
    limparErros();
    const file = event.target.files[0];
    const previewId = 'image-preview';
    let preview = document.getElementById(previewId);
    const labelText = document.getElementById('file-label-text');

    if (labelText) {
        if (file) {
            labelText.textContent = file.name;
            labelText.classList.add('has-file');
        }
        else {
            labelText.textContent = 'Escolher Evidência (Foto)';
            labelText.classList.remove('has-file');
        }
    }

    if (!preview) {
        preview = document.createElement('img');
        preview.id = previewId;
        preview.style.maxWidth = '200px';
        preview.style.display = 'none';
        preview.className = 'mt-3 border rounded';
        event.target.closest('.input-group-wrapper').after(preview);
    }

    if (!file) {
        preview.style.display = 'none';
        preview.src = '';
        return;
    }

    const reader = new FileReader();
    reader.onload = function(e) {
        preview.src = e.target.result;
        preview.style.display = 'block';
    };
    reader.readAsDataURL(file);
});

document.getElementById('id_evento').addEventListener('change', function(event) {
    limparErros();
    const idEventoSelecionado = event.target.value;
    if (idEventoSelecionado)
        carregarAtividades(idEventoSelecionado);
});

document.getElementById('id_atividade').addEventListener('change', function(event) {
    limparErros();
    const idAtividade = event.target.value;
    if (idAtividade)
        carregarFotos(idAtividade);
});

document.getElementById('id_membro').addEventListener('input', limparErros);

document.getElementById('upload-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    limparErros();

    const idAtividade = document.getElementById('id_atividade');
    const idMembro = document.getElementById('id_membro');
    const fotoInput = document.getElementById('foto');

    let temErro = false;
    if (!document.getElementById('id_evento').value) {
        document.getElementById('id_evento').classList.add('is-invalid');
        temErro = true;
    }
    if (!idAtividade.value) {
        idAtividade.classList.add('is-invalid');
        temErro = true;
    }
    if (!idMembro.value) {
        idMembro.classList.add('is-invalid');
        temErro = true;
    }
    if (fotoInput.files.length === 0) {
        const label = document.getElementById('file-label-text');
        const feedback = document.getElementById('feedback-foto');
        if (label)
            label.classList.add('is-invalid-custom');
        if (feedback)
            feedback.classList.remove('d-none');
        temErro = true;
    }

    if (temErro)
        return;

    const formData = new FormData();
    formData.append('id_membro', idMembro.value);
    formData.append('foto', fotoInput.files[0]);

    const API_URL = `/apis/atividade/${idAtividade.value}/fotos`;

    try {
        const response = await fetch(API_URL, {
            method: 'POST',
            body: formData
        });

        if (!response.ok) {
            let errorMsg = 'Falha no upload';
            try {
                const erro = await response.json();
                errorMsg = erro.erro || erro.message || 'Falha no upload';
            } catch(e) { errorMsg = `Status: ${response.status}`; }
            throw new Error(errorMsg);
        }

        const resultado = await response.json();
        mostrarMensagem(`Foto enviada com sucesso!`, 'success');

        document.getElementById('upload-form').reset();

        const preview = document.getElementById('image-preview');
        if (preview) {
            preview.style.display = 'none';
            preview.src = '';
        }

        const labelText = document.getElementById('file-label-text');
        if (labelText) {
            labelText.textContent = 'Escolher Evidência (Foto)';
            labelText.classList.remove('has-file');
        }

        carregarFotos(idAtividade.value);

    } catch (error) {
        console.error('Erro:', error);
        mostrarMensagem(error.message, 'danger');
    }
});

function mostrarMensagem(texto, tipo) {
    msgFeedback.textContent = texto;
    msgFeedback.className = `alert alert-${tipo}`;
    msgFeedback.classList.remove('d-none');
    window.scrollTo({ top: 0, behavior: 'smooth' });
    if(tipo === 'success')
        setTimeout(() => { msgFeedback.classList.add('d-none'); }, 5000);
}

function limparErros() {
    msgFeedback.classList.add('d-none');
    document.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));
    const label = document.getElementById('file-label-text');
    const feedbackFoto = document.getElementById('feedback-foto');
    if (label)
        label.classList.remove('is-invalid-custom');
    if (feedbackFoto)
        feedbackFoto.classList.add('d-none');
}

carregarEventos();