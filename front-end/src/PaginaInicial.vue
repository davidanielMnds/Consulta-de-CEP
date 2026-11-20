<script lang="ts" setup>
import MenuConsulta from './components/MenuConsulta.vue'
import MenuAside from './components/MenuAside.vue'

</script>

<template>

  <div id="root">

    <header>

      <h1>Consulta de CEP</h1>

    </header>

    <!-- < Caixa Principal /> -->
    <div id="content">

      <!-- Caixa de Consulta -->
      <main>
        <MenuConsulta />
      </main>

      <!-- Caixa de Menu Lateral -->
      <aside>
        <MenuAside />
      </aside>

    </div>

  </div>

</template>

<style lang="css" scoped>

header {
  display: flex;
  justify-content: center;
  background-color:gray
}

#content{
  background-color: red;
}

main{
  background-color: blue;
}

aside{
  background-color: green;
}

</style>