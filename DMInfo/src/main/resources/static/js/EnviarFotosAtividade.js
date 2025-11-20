async function carregarEventos() {
    const selectEvento = document.getElementById('id_evento');
    try {
        const response = await fetch('/apis/eventos');
        if (!response.ok)
            throw new Error('Falha ao carregar eventos');

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
        console.error('Erro ao carregar eventos:', error);
        selectEvento.innerHTML = '<option value="" disabled selected>Erro ao carregar eventos</option>';
        selectEvento.disabled = true;
    }
}

async function carregarAtividades(idEvento) {
    const selectAtividade = document.getElementById('id_atividade');
    selectAtividade.innerHTML = '<option value="" disabled selected>Carregando atividades...</option>';
    selectAtividade.disabled = true;

    try {
        const response = await fetch(`/apis/eventos/${idEvento}/atividades`);
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
        console.error('Erro ao carregar atividades:', error);
        selectAtividade.innerHTML = '<option value="" disabled selected>Erro ao carregar atividades</option>';
    }
}

document.getElementById('foto').addEventListener('change', function(event) {
    const file = event.target.files[0];
    const previewId = 'image-preview';
    let preview = document.getElementById(previewId);

    const labelText = document.getElementById('file-label-text');
    if (labelText) {
        if (file) {
            labelText.textContent = file.name;
            labelText.classList.add('has-file');
        } else {
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
    const idEventoSelecionado = event.target.value;
    if (idEventoSelecionado)
        carregarAtividades(idEventoSelecionado);
});

document.getElementById('upload-form').addEventListener('submit', async (e) => {
    e.preventDefault();

    const idAtividade = document.getElementById('id_atividade').value;
    const idMembro = document.getElementById('id_membro').value;
    const fotoInput = document.getElementById('foto');

    if (fotoInput.files.length === 0) {
        alert("Por favor, selecione um arquivo de foto.");
        return;
    }
    if (!idAtividade || !idMembro) {
        alert("Por favor, selecione o Evento e a Atividade, e verifique o ID do Membro.");
        return;
    }

    const formData = new FormData();
    formData.append('id_membro', idMembro);
    formData.append('foto', fotoInput.files[0]);

    const API_URL = `/apis/atividade/${idAtividade}/fotos`;

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
            } catch(e) {
                errorMsg = `Falha no upload: ${response.status} ${response.statusText}`;
            }
            throw new Error(errorMsg);
        }

        const resultado = await response.json();
        alert('Foto enviada com sucesso! ID da Foto: ' + resultado.id);

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

    } catch (error) {
        console.error('Erro:', error);
        alert('Erro: ' + error.message);
    }
});

carregarEventos();