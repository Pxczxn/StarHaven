import { useMemo } from 'react';
import './CosmicUniverse.css';

export const CosmicUniverse = () => {
  // 🚀 高性能：用 useMemo 在组件挂载时只生成一次随机星轨，避免二次渲染重置星星位置
  const starStyles = useMemo(() => {
    // 随机坐标生成器
    const generateStars = (count, color = '#ffffff') => {
      const stars = [];
      for (let i = 0; i < count; i++) {
        const x = Math.floor(Math.random() * 2000);
        const y = Math.floor(Math.random() * 2000);
        stars.push(`${x}px ${y}px ${color}`);
      }
      return stars.join(', ');
    };

    return {
      tiny: generateStars(120, '#ffffff'),           // 120颗 远景细碎星尘
      medium: generateStars(40, '#06b6d4'),          // 40颗 中景极光青恒星
      blinking: generateStars(25, '#f43f5e'),        // 25颗 近景超新星（粉/白交错）
    };
  }, []);

  return (
    <div className="user-cosmic-universe">
      {/* 远景星尘层 */}
      <div
        className="stars-layer tiny-stars"
        style={{ '--star-shadows': starStyles.tiny }}
      />
      {/* 中景恒星层 */}
      <div
        className="stars-layer medium-stars"
        style={{ '--star-shadows': starStyles.medium }}
      />
      {/* 近景闪烁层 */}
      <div
        className="stars-layer blinking-stars"
        style={{ '--star-shadows': starStyles.blinking }}
      />
    </div>
  );
};
