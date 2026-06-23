import { RouterProvider } from 'react-router-dom';
import { CosmicUniverse } from './components/CosmicUniverse';
import router from './router';
import './styles/global.css';
import './App.css';

function App() {
  return (
    <>
      {/* 动态星空画布（死死钉在最底层） */}
      <CosmicUniverse />

      {/* 前台主内容区域（凌空浮起） */}
      <div className="user-shell">
        <RouterProvider router={router} />
      </div>
    </>
  );
}

export default App;
