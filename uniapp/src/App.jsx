import heroImg from './assets/hero.png'
import './App.css'

function App() {
  return (
    <main className="site-shell">
      <section className="hero-section">
        <img src={heroImg} className="hero-image" alt="星栖民宿" />
        <div className="hero-content">
          <p className="eyebrow">星栖民宿</p>
          <h1>星辰为引，栖心而居</h1>
          <p className="summary">
            租客端 H5 正在建设中，后续将提供房型浏览、在线预订和订单查询。
          </p>
        </div>
      </section>

      <section className="feature-grid" aria-label="核心功能">
        <article>
          <span>01</span>
          <h2>浏览房型</h2>
          <p>展示房间图片、价格、可住人数和配套设施。</p>
        </article>
        <article>
          <span>02</span>
          <h2>在线预订</h2>
          <p>选择入住日期后提交联系人和入住需求。</p>
        </article>
        <article>
          <span>03</span>
          <h2>订单查询</h2>
          <p>通过订单号和手机号查询预订状态。</p>
        </article>
      </section>
    </main>
  )
}

export default App
